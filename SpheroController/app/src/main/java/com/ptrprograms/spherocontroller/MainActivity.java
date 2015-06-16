package com.ptrprograms.spherocontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.ptrprograms.spherocontroller.utils.GamepadController;
import com.ptrprograms.spherocontroller.utils.Utils;

import java.util.List;

import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.sensor.LocatorData;
import orbotix.sphero.ConnectionListener;
import orbotix.sphero.DiscoveryListener;
import orbotix.sphero.LocatorListener;
import orbotix.sphero.Sphero;

public class MainActivity extends Activity implements DiscoveryListener, ConnectionListener, LocatorListener {

    private Sphero mSphero;
    private GamepadController mController = new GamepadController();
    private boolean isRolling = false;
    private float mHeading = 0.0f;
    private float mBacklightLedBrightness = 1.0f;

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
    protected void onPause() {
        super.onPause();
        mSphero.getSensorControl().removeLocatorListener(this);
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

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        if( mSphero == null )
            return super.dispatchGenericMotionEvent(ev);

        Log.e( "Sphero", "dispatchGenericMotionEvent" );
        mController.handleMotionEvent( ev );
        handleJoystick1();
        handleJoystick2();
        return super.dispatchGenericMotionEvent(ev);
    }

    private void handleJoystick1() {
        float newHeadingX = mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_X);
        float newHeadingY = -mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_Y);

        float magnitude = Utils.vector2DLength( newHeadingX, newHeadingY );
        if( magnitude > GamepadController.JOYSTICK_MOVEMENT_THRESHOLD ) {
            mHeading = Utils.getAngleFromHeadings(newHeadingX, newHeadingY);
            mSphero.rotate( mHeading );
        }
    }

    private void handleJoystick2() {
        float newHeadingX = mController.getJoystickPosition(GamepadController.JOYSTICK_2,
                GamepadController.AXIS_X);
        float newHeadingY = -mController.getJoystickPosition(GamepadController.JOYSTICK_2,
                GamepadController.AXIS_Y);

        float magnitude = Utils.vector2DLength( newHeadingX, newHeadingY );
        if( magnitude < GamepadController.JOYSTICK_MOVEMENT_THRESHOLD ) {
            Log.e( "Sphero", "Stopping due to magnitude" );
            mSphero.stop();
            isRolling = false;
        } else if( !isRolling ) {
            //Log.e("Sphero", "Degree: " + (52 * Math.atan2(newHeadingX, newHeadingY) + 90));
            mSphero.drive(mHeading + Utils.getAngleFromHeadings(newHeadingX, newHeadingY), 1.0f );
            isRolling = true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if( mSphero == null )
            return super.dispatchKeyEvent(event);

        mController.handleKeyEvent(event);
        Log.e("Sphero", "DispatchKeyEvent");
        if( mController.isButtonDown( GamepadController.BUTTON_A ) ) {
            //green
            Log.e( "Sphero", "A" );
            mSphero.setColor( 0, 255, 0 );
            return true;
        } else if( mController.isButtonDown( GamepadController.BUTTON_B ) ) {
            //red
            Log.e( "Sphero", "B" );
            mSphero.setColor( 255, 0, 0 );
            return true;
        } else if( mController.isButtonDown( GamepadController.BUTTON_X ) ) {
            //blue
            Log.e( "Sphero", "X" );
            mSphero.setColor( 0, 0, 255 );
            return true;
        } else if( mController.isButtonDown( GamepadController.BUTTON_Y ) ) {
            //yellow
            Log.e( "Sphero", "Y" );
            mSphero.setColor( 255, 255, 0 );
            return true;
        } else if( mController.isButtonDown( GamepadController.BUTTON_L1 ) ) {
            if( mBacklightLedBrightness > 0.0f ) {
                mBacklightLedBrightness -= 0.1f;
                mSphero.setBackLEDBrightness( mBacklightLedBrightness );
            }
            return true;
        } else if( mController.isButtonDown( GamepadController.BUTTON_R1 ) ) {
            if( mBacklightLedBrightness < 1.0f ) {
                mBacklightLedBrightness += 0.1f;
                mSphero.setBackLEDBrightness( mBacklightLedBrightness );
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initSphero() {
        mSphero.getSensorControl().addLocatorListener( this );
        mSphero.getSensorControl().setRate( 1 );
        mSphero.enableStabilization( true );
        mSphero.setBackLEDBrightness( mBacklightLedBrightness );
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

    @Override
    public void onLocatorChanged(LocatorData locatorData) {
        /*
        if( isRolling && locatorData.getVelocityY() < 0.1f && locatorData.getVelocityX() < 0.1f ) {
            Log.e( "Sphero", "onLocatorChanged Stopping" );
            mSphero.stop();
            isRolling = false;
        }
        */
    }
}
