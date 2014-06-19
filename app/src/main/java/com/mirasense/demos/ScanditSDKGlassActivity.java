package com.mirasense.demos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Main activity.
 */
public class ScanditSDKGlassActivity extends Activity implements ScanditSDKListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // The main object for recognizing a displaying barcodes.
    private ScanditSDK mBarcodePicker;
    private Button mButton;

    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is available via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "xAjTvPA7EeOMXLoZ51Sr19zZeNwAStF2RUGkmpjcKB4";

    // Glass Card-related objects.
    private Card _card;
    private View _cardView;
    private TextView _statusTextView;

    // System related variables.
    private Context _context = this;
    private MediaPlayer rickRolling;
    private Boolean isRickRoll = false;
    private int switcher = 1;

    // Layout Related variables.
    private ImageView activityImage, animatingImage;
    private TextView titleText, subTitle;

    // Speech related variables.
    private TextToSpeech speechInstance;

    // Server related variables.
    public static final String TAG = "ScanditSDKGlassActivity";

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();

        // Says "INITIALIZING SCAN".
        startSpeech("Initializing scan. Searching for targets.");
    }

    @Override
    protected void onResume() {

        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();

        // Says "SCANNING RESUMED".
        startSpeech("Scanning resumed.");

        super.onResume();
    }

    @Override
    protected void onPause() {

        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();

        // Says "SCANNING HAS BEEN TERMINATED".
        startSpeech("Scanning has been terminated. Have a nice day!");

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** PHYSICAL KEY FUNCTIONALITY _____________________________________________________________ **/

    /**
     * Handle the tap event from the touchpad.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            // Handle tap events.
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:

                // Status message below the main text in the alternative UX layout
                AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audio.playSoundEffect(Sounds.TAP);

                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }


    /** BARCODE SCANNING FUNCTIONALITY _________________________________________________________ **/

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // We instantiate the automatically adjusting barcode picker that will
        // choose the correct picker to instantiate. Be aware that this picker
        // should only be instantiated if the picker is shown full screen as the
        // legacy picker will rotate the orientation and not properly work in
        // non-fullscreen.
        ScanditSDKAutoAdjustingBarcodePicker picker = new ScanditSDKAutoAdjustingBarcodePicker(
                this, sScanditSdkAppKey, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);

        // Add both views to activity, with the scan GUI on top.
        setContentView(picker);
        mBarcodePicker = picker;

        mBarcodePicker.getOverlayView().setBeepEnabled(false);
        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.getOverlayView().addListener(this);

        // Set all settings according to the settings activity. Normally there will be no settings
        // activity for the picker and you just hardcode the setting your app needs.
        SettingsActivity.setSettingsForPicker(this, mBarcodePicker);
    }

    /**
     *  Called when a barcode has been decoded successfully.
     *
     *  @param barcode Scanned barcode content.
     *  @param symbology Scanned barcode symbology.
     */
    public void didScanBarcode(String barcode, String symbology) {
        // Remove non-relevant characters that might be displayed as rectangles
        // on some devices. Be aware that you normally do not need to do this.
        // Only special GS1 code formats contain such characters.

        // JSON List String
        List<String> barcodeList = new LinkedList<String>();

        // The barcode string.
        String cleanedBarcode = "";

        for (int i = 0 ; i < barcode.length(); i++) {
            if (barcode.charAt(i) > 30) {
                cleanedBarcode += barcode.charAt(i);
            }
        }

        // Adds the barcode to the list.
        barcodeList.add(new String(cleanedBarcode));

        mButton = new Button(this);
        mButton.setTextColor(Color.WHITE);
        mButton.setTextSize(30);
        mButton.setGravity(Gravity.CENTER);
        mButton.setBackgroundColor(0xAA000000);
        mButton.setText("Scanned " + symbology + " code:\n\n" + cleanedBarcode + "\n\n\n\nTap to continue");
        ((RelativeLayout) mBarcodePicker).addView(
                mButton, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mBarcodePicker.stopScanning();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarcodePicker.startScanning();
                ((RelativeLayout) mBarcodePicker).removeView(mButton);
                mButton = null;
            }
        });
        mButton.requestFocus();

        /** SERVER RESPONSE CODE ** EXPERIMENTAL **/
        //serverResponder(cleanedBarcode);

        ServerResponder.updateServer(cleanedBarcode,
                barcodeList,
                ScanditSDKGlassActivity.this);


        // Example code that would typically be used in a real-world app using
        // the Scandit SDK.
        /*
        // Access the image in which the bar code has been recognized.
        byte[] imageDataNV21Encoded = barcodePicker.getCameraPreviewImageOfFirstBarcodeRecognition();
        int imageWidth = barcodePicker.getCameraPreviewImageWidth();
        int imageHeight = barcodePicker.getCameraPreviewImageHeight();

        // Stop recognition to save resources.
        mBarcodePicker.stopScanning();
        */
    }

    /**
     * Called when the user entered a bar code manually.
     *
     * @param entry The information entered by the user.
     */
    public void didManualSearch(String entry) {
        // Example code that would typically be used in a real-world app using
        // the Scandit SDK.

        Toast.makeText(this, "User entered: " + entry, Toast.LENGTH_LONG).show();
    }

    @Override
    public void didCancel() {
        mBarcodePicker.stopScanning();
        finish();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }

    /** SERVER RESPONSE FUNCTIONALITY __________________________________________________________ **/

    private void serverResponder(String barcodeID) {

        final List<String> barcodeList = new LinkedList<String>();

        ServerResponder.updateServer(barcodeID,
                barcodeList,
                ScanditSDKGlassActivity.this);
    }

    /** ADDITIONAL FUNCTIONALITY _______________________________________________________________ **/

    // startSpeech(): Activates the TTK voice functionality to say something.
    private void startSpeech(final String script) {

        speechInstance = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                speechInstance.speak(script, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    /** EXTENSION FUNCNTIONALITY _______________________________________________________________ **/

    public void onServerUpdated() {
        Log.d(TAG, "onServerUpdated");
    }

    public void onServerUpdateError() {
        Log.d(TAG, "onServerUpdateError");

    }

}
