package de.pacheco.fpdmockup

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {
    private val TAG = "FPDmockup"
    private var player: ExoPlayer? = null
    private var videoContainer: FrameLayout? = null
    private var hideUi: Button? = null
    private var dpi: EditText? = null
    private var heightInput: EditText? = null
    private var aspectRatioWidthInput: EditText? = null
    private var aspectRatioHeightInput: EditText? = null
    private var formatSwitch: Switch? = null
    private var playerView: PlayerView? = null
    private var btn12_21_9: View? = null
    private var btn12_16_9: View? = null
    private var btn15_175_9: View? = null
    private var btn12_175_9: View? = null
    private var isTypingText = false
    private var changeVideo: View? = null
    private val sintel219: MediaItem = MediaItem.fromUri(
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .path(R.raw.sintel219.toString()).build()
    )
    private val sintel169 = MediaItem.fromUri(
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .path(R.raw.sintel169.toString()).build()
    )
    private val spring169: MediaItem = MediaItem.fromUri(
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .path(R.raw.spring169.toString()).build()
    )
    private val spring219 = MediaItem.fromUri(
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .path(R.raw.spring219.toString()).build()
    )
    private var mediaItem16_9: MediaItem = spring169
    private var mediaItem21_9: MediaItem = spring219

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()

        playerView = findViewById<PlayerView?>(R.id.player_view)
        videoContainer = findViewById<FrameLayout?>(R.id.video_container)
        hideUi = findViewById<Button>(R.id.hideUI)
        dpi = findViewById<EditText?>(R.id.dpi)
        heightInput = findViewById<EditText?>(R.id.height_input)
        aspectRatioWidthInput = findViewById<EditText?>(R.id.aspect_ratio_width_input)
        aspectRatioHeightInput = findViewById<EditText?>(R.id.aspect_ratio_height_input)
        formatSwitch = findViewById<Switch?>(R.id.format_switch)
        btn12_21_9 = findViewById<View?>(R.id.button_12cm_21_9)
        btn12_16_9 = findViewById<View?>(R.id.button_12cm_16_9)
        btn15_175_9 = findViewById<View?>(R.id.button_15cm_17_5_9)
        btn12_175_9 = findViewById<View?>(R.id.button_12cm_17_5_9)
        btn12_175_9 = findViewById<View?>(R.id.button_12cm_17_5_9)
        changeVideo = findViewById<View?>(R.id.changeVideo)

        startPlayer()
        getMetrics()
        setupButtons()
        setupTextWatchers()
        setupSwitch()
        updateBoxSize(12.0, 21.0, 9.0)
    }

    private fun startPlayer() {
        Log.d(TAG, "startPlayer called")
        player = ExoPlayer.Builder(this).build()
        playerView?.setPlayer(player)
        //      val videoUri: Uri =
        //        android.net.Uri.parse(
        //            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        //      val mediaItem: MediaItem = MediaItem.fromUri(videoUri)
        player?.setMediaItem(mediaItem16_9)
        player?.prepare()
        player?.play()
    }

    private fun hideSystemUI() {
        Log.d(TAG, "hideSystemUI called")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun setupButtons() {
        Log.d(TAG, "setupButtons called")
        changeVideo?.setOnClickListener { _ ->
            changeVideo()
        }
        hideUi?.setOnClickListener { v: View? ->
            hideUi()
        }
        btn12_21_9?.setOnClickListener { v: View? ->
            updateBoxSize(12.0, 21.0, 9.0)
        }
        btn12_16_9?.setOnClickListener { v: View? ->
            updateBoxSize(12.0, 16.0, 9.0)
        }
        btn15_175_9?.setOnClickListener { v: View? ->
            updateBoxSize(15.0, 17.5, 9.0)
        }
        btn12_175_9?.setOnClickListener { v: View? ->
            updateBoxSize(12.0, 17.5, 9.0)
        }
    }

    private fun hideUi() {
        Log.d(TAG, "hideUi called")
        val ui = findViewById<View?>(R.id.ui)
        if (ui?.visibility == View.INVISIBLE) {
            ui.visibility = View.VISIBLE
            hideUi?.setTextColor(ContextCompat.getColor(this, R.color.white))
            hideUi?.text="Hide UI"
        } else {
            ui?.visibility = View.INVISIBLE
            hideUi?.setTextColor(ContextCompat.getColor(this, R.color.halfblack))
            hideUi?.text="Show UI"
        }
    }

    private fun changeVideo() {
        Log.d(TAG, "changeVideo called")
        if (mediaItem16_9 == spring169) {
            mediaItem16_9 = sintel169
            mediaItem21_9 = sintel219
        } else {
            mediaItem16_9 = spring169
            mediaItem21_9 = spring219
        }
        updatePlayer()
    }

    private fun setupTextWatchers() {
        Log.d(TAG, "setupTextWatchers called")
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                isTypingText = true
                updateBoxFromInputs()
                isTypingText = false
            }
        }

        dpi?.addTextChangedListener(textWatcher)
        heightInput?.addTextChangedListener(textWatcher)
        aspectRatioHeightInput?.addTextChangedListener(textWatcher)
        aspectRatioWidthInput?.addTextChangedListener(textWatcher)
    }

    private fun getMetrics() {
        Log.d(TAG, "getMetrics called")
        //beides falsch wenn die im AOSP falsch eingetragen sind
        //      val densityDpi = getResources().getDisplayMetrics().densityDpi
        //        val densityDpi = DisplayMetrics.DENSITY_DEFAULT * windowManager.getCurrentWindowMetrics().getDensity()
        val metrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getRealMetrics(metrics)
        val densityDpi = metrics.ydpi
        dpi?.setText(densityDpi.toString())
    }

    private fun setupSwitch() {
        Log.d(TAG, "setupSwitch called")
        formatSwitch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            formatSwitch?.setText(if (isChecked) "21:9" else "16:9")
            updatePlayer()
        })
    }

    private fun updateBoxFromInputs() {
        Log.d(TAG, "updateBoxFromInputs called")
        try {
            val heightCm: Double = heightInput?.getText().toString().toDouble()
            val aspectRatioWidth: Double = aspectRatioWidthInput?.getText().toString().toDouble()
            val aspectRatioHeight: Double = aspectRatioHeightInput?.getText().toString().toDouble()
            updateBoxSize(heightCm, aspectRatioWidth, aspectRatioHeight)
        } catch (e: NumberFormatException) {
        }
    }

    private fun updateBoxSize(
        heightCm: Double, aspectRatioWidth: Double, aspectRatioHeight: Double
    ) {
        Log.d(TAG, "updateBoxSize called")
        val densityDpi = dpi?.text.toString().toFloat()
        val heightPx: Double = heightCm * densityDpi / 2.54
        val widthPx = heightPx * (aspectRatioWidth / aspectRatioHeight)
        val params: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(widthPx.toInt(), heightPx.toInt())
        params.leftMargin = (getResources().getDisplayMetrics().widthPixels - widthPx.toInt()) / 2
        //params.topMargin = (getResources().getDisplayMetrics().heightPixels - heightPx.toInt())
        videoContainer?.setLayoutParams(params)
        updateTextFields(heightCm, aspectRatioWidth, aspectRatioHeight)
    }

    private fun updateTextFields(
        heightCm: Double, aspectRatioWidth: Double, aspectRatioHeight: Double
    ) {
        Log.d(TAG, "updateTextFields called")
        if (isTypingText) return
        heightInput?.setText(heightCm.toString())
        aspectRatioWidthInput?.setText(aspectRatioWidth.toString())
        aspectRatioHeightInput?.setText(aspectRatioHeight.toString())
    }

    private fun updatePlayer() {
        Log.d(TAG, "updatePlayer called")
        val currentPosition = player?.currentPosition ?: 0L
        val wasPlaying = player?.isPlaying ?: true
        if ("21:9" == formatSwitch?.getText()) {
            player?.setMediaItem(mediaItem21_9)
        } else {
            player?.setMediaItem(mediaItem16_9)
        }
        player?.prepare()
        player?.seekTo(currentPosition)
        if (wasPlaying) player?.play()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        player?.release()
    }
}
