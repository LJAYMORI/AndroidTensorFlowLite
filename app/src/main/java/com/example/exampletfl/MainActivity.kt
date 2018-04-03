package com.example.exampletfl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore


class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_button.setOnClickListener {
            disposable += RxImagePicker.with(this@MainActivity)
                    .requestImage(Sources.GALLERY)
                    .firstOrError()
                    // load image to ImageView
                    .doOnEvent { uri, _ -> main_image.setImageURI(uri) }
                    .observeOn(Schedulers.io())
                    // update stream for classify image
                    .map { MediaStore.Images.Media.getBitmap(this.contentResolver, it) }
                    .map { ImageClassifier(this@MainActivity).classifyFrame(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ main_text.text = it }, Throwable::printStackTrace)
        }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

}
