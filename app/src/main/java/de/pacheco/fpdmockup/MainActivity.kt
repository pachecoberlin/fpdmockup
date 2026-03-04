package de.pacheco.fpdmockup

import android.os.Bundle
import android.os.Environment
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
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

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
  private var button_184_16_9: View? = null
  private var button_195_16_9: View? = null
  private var button_214_16_9: View? = null
  private var button_195_21_9: View? = null
  private var button_203_21_9: View? = null
  private var isTypingText = false
  private val base =
      File(Environment.getExternalStorageDirectory(), "Android/media/de.pacheco.fpdmockup/videos")
  private var mediaItem16_9 = getMediaItem("169stunning.mp4")
  private var mediaItem21_9 = getMediaItem("219stunning.mp4")
  private var darkenOverlay: View? = null
  private lateinit var brightnessSlider: com.google.android.material.slider.Slider
  private val MIN_ALPHA = 0.0f
  private val MAX_ALPHA = 0.9f

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
    button_184_16_9 = findViewById<View?>(R.id.button_184_16_9)
    button_195_16_9 = findViewById<View?>(R.id.button_195_16_9)
    button_214_16_9 = findViewById<View?>(R.id.button_214_16_9)
    button_195_21_9 = findViewById<View?>(R.id.button_195_21_9)
    button_203_21_9 = findViewById<View?>(R.id.button_203_21_9)
    darkenOverlay = findViewById(R.id.darkenOverlay)
    brightnessSlider = findViewById(R.id.brightnessSlider)

    initBrightnessControl()
    startPlayer()
    getMetrics()
    setupButtons()
    setupTextWatchers()
    setupSwitch()
    updateBoxSize(22.9, 16.0, 9.0)
  }

  private fun startPlayer() {
    Log.d(TAG, "startPlayer called")
    player = ExoPlayer.Builder(this).build()
    playerView?.setPlayer(player)
    //      val videoUri: Uri =
    //        android.net.Uri.parse(
    //
    // "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
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
    hideUi?.setOnClickListener { v: View? -> hideUi() }
    button_184_16_9?.setOnClickListener { v: View? -> updateBoxSize(22.9, 16.0, 9.0) }
    button_195_16_9?.setOnClickListener { v: View? -> updateBoxSize(24.3, 16.0, 9.0) }
    button_214_16_9?.setOnClickListener { v: View? -> updateBoxSize(26.6, 16.0, 9.0) }
    button_195_21_9?.setOnClickListener { v: View? -> updateBoxSize(19.5, 21.0, 9.0) }
    button_203_21_9?.setOnClickListener { v: View? -> updateBoxSize(20.3, 21.0, 9.0) }
  }

  private fun hideUi() {
    Log.d(TAG, "hideUi called")
    val ui = findViewById<View?>(R.id.ui)
    if (ui?.visibility == View.INVISIBLE) {
      ui.visibility = View.VISIBLE
      hideUi?.setTextColor(ContextCompat.getColor(this, R.color.white))
      hideUi?.text = "Hide UI"
    } else {
      ui?.visibility = View.INVISIBLE
      hideUi?.setTextColor(ContextCompat.getColor(this, R.color.halfblack))
      hideUi?.text = "Show UI"
    }
  }

  private fun setupTextWatchers() {
    Log.d(TAG, "setupTextWatchers called")
    val textWatcher: TextWatcher =
        object : TextWatcher {
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
    // beides falsch wenn die im AOSP falsch eingetragen sind
    //      val densityDpi = getResources().getDisplayMetrics().densityDpi
    //        val densityDpi = DisplayMetrics.DENSITY_DEFAULT *
    // windowManager.getCurrentWindowMetrics().getDensity()
    val metrics = DisplayMetrics()
    windowManager.getDefaultDisplay().getRealMetrics(metrics)
    val densityDpi = metrics.ydpi
    dpi?.setText(densityDpi.toString())
  }

  private fun setupSwitch() {
    Log.d(TAG, "setupSwitch called")
    formatSwitch?.setOnCheckedChangeListener(
        CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
          formatSwitch?.setText(if (isChecked) "21:9" else "16:9")
          updatePlayer()
        }
    )
  }

  private fun updateBoxFromInputs() {
    Log.d(TAG, "updateBoxFromInputs called")
    try {
      val heightCm: Double = heightInput?.getText().toString().toDouble()
      val aspectRatioWidth: Double = aspectRatioWidthInput?.getText().toString().toDouble()
      val aspectRatioHeight: Double = aspectRatioHeightInput?.getText().toString().toDouble()
      updateBoxSize(heightCm, aspectRatioWidth, aspectRatioHeight)
    } catch (e: NumberFormatException) {}
  }

  private fun updateBoxSize(heightCm: Double, aspectRatioWidth: Double, aspectRatioHeight: Double) {
    Log.d(TAG, "updateBoxSize called")
    val densityDpi = dpi?.text.toString().toFloat()
    val heightPx: Double = heightCm * densityDpi / 2.54
    val widthPx = heightPx * (aspectRatioWidth / aspectRatioHeight)
    val params: RelativeLayout.LayoutParams =
        RelativeLayout.LayoutParams(widthPx.toInt(), heightPx.toInt())
    params.leftMargin = (getResources().getDisplayMetrics().widthPixels - widthPx.toInt()) / 2
    // params.topMargin = (getResources().getDisplayMetrics().heightPixels - heightPx.toInt())
    videoContainer?.setLayoutParams(params)
    updateTextFields(heightCm, aspectRatioWidth, aspectRatioHeight)
  }

  private fun updateTextFields(
      heightCm: Double,
      aspectRatioWidth: Double,
      aspectRatioHeight: Double,
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

  private fun Float.toOverlayAlpha(): Float {
    return (MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * this).coerceIn(MIN_ALPHA, MAX_ALPHA)
  }

  private fun Float.toSliderValue(): Float {
    val ratio = (this - MIN_ALPHA) / (MAX_ALPHA - MIN_ALPHA)
    return ratio.coerceIn(0f, 1f)
  }

  private fun initBrightnessControl() {
    Log.d(TAG, "initBrightnessControl called")
    val startAlpha = darkenOverlay?.alpha
    brightnessSlider.value = startAlpha ?: 0.0f.toSliderValue()
    brightnessSlider.addOnChangeListener { _, value, fromUser ->
      if (fromUser) {
        darkenOverlay?.alpha = value.toOverlayAlpha()
      } else {
        darkenOverlay?.alpha = value.toOverlayAlpha()
      }
    }
    // Slider ein-/ausblenden
    playerView?.setControllerVisibilityListener(
        PlayerView.ControllerVisibilityListener { visibility ->
          if (visibility == PlayerView.VISIBLE) {
            brightnessSlider
                .animate()
                .alpha(1f)
                .setDuration(150)
                .withStartAction { brightnessSlider.visibility = View.VISIBLE }
                .start()
          } else {
            brightnessSlider
                .animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction { brightnessSlider.visibility = View.GONE }
                .start()
          }
        }
    )
  }

  private fun getMediaItem(filename: String): MediaItem {
    val file = File(base, filename)
    if (!file.exists()) Log.e(TAG, "File $filename does not exist at ${file.path}")
    return MediaItem.fromUri(file.toUri())
  }
}
