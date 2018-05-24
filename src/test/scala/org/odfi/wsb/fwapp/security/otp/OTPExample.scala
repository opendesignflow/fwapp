package org.odfi.wsb.fwapp.security.otp

import java.security.SecureRandom
import com.google.common.io.BaseEncoding
import com.lochbridge.oath.otp.TOTP
import java.util.concurrent.TimeUnit
import com.lochbridge.oath.otp.keyprovisioning.OTPKey
import com.lochbridge.oath.otp.keyprovisioning.OTPKey.OTPType
import com.lochbridge.oath.otp.keyprovisioning.OTPAuthURIBuilder
import org.odfi.indesign.core.module.jfx.JavaFXUtilsTrait
import javafx.stage.Stage
import javafx.scene.layout.BorderPane
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.image.Image
import com.lochbridge.oath.otp.keyprovisioning.qrcode.QRCodeWriter
import com.lochbridge.oath.otp.keyprovisioning.qrcode.QRCodeWriter.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import org.odfi.indesign.core.module.jfx.JFXRun
import javafx.application.Platform
import java.util.concurrent.Executors
import java.util.Timer
import java.util.TimerTask

object OTPExample extends App with JavaFXUtilsTrait {

  // Create key
  //---------------
  // Step 1: Generate a 160-bit shared secret key.
  var secretKeyBytes = new Array[Byte](20);
  var random = new SecureRandom();
  random.nextBytes(secretKeyBytes);
  var secretKey = BaseEncoding.base32().encode(secretKeyBytes);

  // Create OTP
  //-------------------
  //var builder = TOTP.key(secretKeyBytes).timeStep(TimeUnit.SECONDS.toMillis(30)).digits(6).hmacSha512()
  var builder = TOTP.key(secretKeyBytes).timeStep(TimeUnit.SECONDS.toMillis(30)).digits(6).hmacSha1()
  var totp = builder.build();

  // Create URI
  //-------------------
  var otpKey = new OTPKey(secretKey, OTPType.TOTP);
  var issuer = "Fwapp";
  var label = issuer + ":Example";
  var uri = OTPAuthURIBuilder.fromKey(otpKey).label(label).issuer(issuer)
    .digits(6).timeStep(30000L).build();
  System.out.println(uri.toUriString());
  System.out.println(uri.toPlainTextUriString());

  // Create Image
  //------------------
  var qrWriter = QRCodeWriter.fromURI(uri).width(300).height(300).errorCorrectionLevel(ErrorCorrectionLevel.H)
    .margin(4).imageFormatName("PNG")

  var imageOutBytes = new ByteArrayOutputStream
  qrWriter.write(imageOutBytes)
  imageOutBytes.flush()

  // Create GUI
  //-----------------
  JFXRun.onJavaFX {

    Platform.setImplicitExit(true)
    
    var stage = new Stage
    stage.setWidth(600)
    stage.setHeight(800)

    var bp = new BorderPane
    var scene = new Scene(bp, 600, 800)

    stage.setScene(scene)
    stage.centerOnScreen()
    stage.setResizable(true)

    //-- Image container
    var qrImage = new ImageView
    bp.setCenter(qrImage)

    qrImage.setPreserveRatio(true)

    var imageInBytes = new ByteArrayInputStream(imageOutBytes.toByteArray())
    qrImage.setImage(new Image(imageInBytes))

    //-- Label
    val label = new Label(totp.value())
    bp.setBottom(label)

    //-- Update code label
    /*def codeUpdate : Unit = {
      label.setText(totp.value())
      onJFXThread {
        Thread.sleep(5000)
        codeUpdate
      }
    }
    onJFXThread {
      codeUpdate
      Thread.sleep(5000)
    }*/
    
    stage.show()
    
    var tm = new Timer
    tm.scheduleAtFixedRate(new TimerTask {
      
      def run = {
       // println("Timer executing")
        onJFXThread {
        //  println("Updating Value")
           label.setText(builder.build().value())
        }
       
      }
    }, 0, 2000)

  }

}