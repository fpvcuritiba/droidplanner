package com.diydrones.droidplanner;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.diydrones.droidplanner.dialogs.OpenGcpFileDialog;
import com.diydrones.droidplanner.fragments.GcpMapFragment;
import com.diydrones.droidplanner.fragments.GcpMapFragment.OnGcpClickListner;
import com.diydrones.droidplanner.helpers.KmlParser;
import com.diydrones.droidplanner.waypoints.gcp;

public class GCPActivity extends SuperActivity implements OnGcpClickListner {
	
	public List<gcp> gcpList;
	private GcpMapFragment gcpMapFragment;

	@Override
	int getNavigationItem() {
		return 5;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.gcp);
			
		gcpList = new ArrayList<gcp>();
	
		gcpMapFragment = ((GcpMapFragment)getFragmentManager().findFragmentById(R.id.gcpMapFragment));
		gcpMapFragment.setupMap(PreferenceManager.getDefaultSharedPreferences(this));
		clearWaypointsAndUpdate();
		
		checkIntent();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_gcp, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_clear:
			clearWaypointsAndUpdate();
			return true;
		case R.id.menu_open_kmz:
			openGcpFile();
			return true;
		case R.id.menu_zoom:
			gcpMapFragment.zoomToExtents(gcpList);
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public void openGcpFile() {
		OpenGcpFileDialog dialog = new OpenGcpFileDialog() {			
			@Override
			public void onGcpFileLoaded(List<gcp> list) {
				if(list!=null){
					putListToGcp(list);
				}
			}
		};		
		dialog.openGCPDialog(this);
	}

	private void putListToGcp(List<gcp> list) {
		gcpList.clear();
		gcpList.addAll(list);
		gcpMapFragment.updateMarkers(list);
		gcpMapFragment.zoomToExtents(list);
	}
	
	private void checkIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_VIEW.equals(action) && type != null) {
			Toast.makeText(this, intent.getData().getPath(), Toast.LENGTH_LONG)
					.show();
			KmlParser parser = (new KmlParser());
			boolean fileIsOpen = parser.openGCPFile(intent.getData().getPath());
			if (fileIsOpen) {
				putListToGcp(parser.gcpList);
			}
		}
	}

	public void clearWaypointsAndUpdate() {
		gcpList.clear();
		gcpMapFragment.updateMarkers(gcpList);
	}

	@Override
	public void onGcpClick(int number) {
		gcpList.get(number).toogleState();
		gcpMapFragment.updateMarkers(gcpList);		
	}

}