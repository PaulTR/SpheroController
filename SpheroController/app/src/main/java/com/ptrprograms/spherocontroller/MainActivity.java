package com.ptrprograms.spherocontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.sphero.ConnectionListener;
import orbotix.sphero.DiscoveryListener;
import orbotix.sphero.Sphero;

public class MainActivity extends Activity implements DiscoveryListener, ConnectionListener {

    private Sphero mSphero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Sphero", "onResume");
        RobotProvider.getDefaultProvider().addConnectionListener( this );
        RobotProvider.getDefaultProvider().addDiscoveryListener( this );
        RobotProvider.getDefaultProvider().startDiscovery( this );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( mSphero != null )
            mSphero.disconnect();

        RobotProvider.getDefaultProvider().removeConnectionListener( this );
        RobotProvider.getDefaultProvider().removeDiscoveryListener( this );
        RobotProvider.getDefaultProvider().endDiscovery();
        RobotProvider.getDefaultProvider().shutdown();
    }

    private void initSphero() {
        mSphero.enableStabilization( true );
        mSphero.setBackLEDBrightness(1.0f);
        mSphero.setColor( 0, 255, 0 );
    }

    @Override
    public void onConnected(Robot robot) {
        Log.e( "Sphero", "onConnected" );
        mSphero = (Sphero) robot;
        initSphero();
    }

    @Override
    public void onConnectionFailed(Robot robot) {
        Log.e( "Sphero", "onConnectionFailed" );
    }

    @Override
    public void onDisconnected(Robot robot) {
        Log.e( "Sphero", "onDisconnected" );

    }

    @Override
    public void onBluetoothDisabled() {
        Log.e( "Sphero", "onBluetoothDisabled" );
    }

    @Override
    public void discoveryComplete(List<Sphero> list) {
        Log.e( "Sphero", "discoveryComplete" );
        for( Sphero sphero : list ) {
            Log.e( "Sphero", sphero.getName() );
        }
    }

    @Override
    public void onFound(List<Sphero> list) {
        Log.e("Sphero", "onFound");
        for (Sphero sphero : list) {
            Log.e("Sphero", sphero.getName());
            RobotProvider.getDefaultProvider().connect(list.iterator().next());
        }
    }
}
