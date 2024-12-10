package com.example.solar_system_scope_app

import android.content.Context
import android.opengl.Matrix
import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.gltfio.*
import java.nio.ByteBuffer

class BackgroundLoader(
    private val context: Context,
    private val engine: Engine,
    private val scene: Scene,
    private val assetLoader: AssetLoader,
    private val resourceLoader: ResourceLoader
) {
    private var backgroundAsset: FilamentAsset? = null

    fun loadBackground(fileName: String) {
        Log.d("BackgroundLoader", "Bắt đầu load nền GLB: $fileName")
        val buffer = readAsset(context, fileName)
        backgroundAsset = assetLoader.createAsset(ByteBuffer.wrap(buffer))

        if (backgroundAsset == null) {
            Log.e("BackgroundLoader", "Không thể tạo asset từ file GLB")
            throw IllegalStateException("Không thể tải mô hình nền GLTF.")
        }

        resourceLoader.loadResources(backgroundAsset!!)
        scene.addEntities(backgroundAsset!!.entities)
        Log.d("BackgroundLoader", "Đã thêm nền vào scene")

        adjustBackgroundTransform()

    }

    private fun adjustBackgroundTransform() {
        val transformManager = engine.transformManager
        val rootEntity = backgroundAsset!!.root
        val instance = transformManager.getInstance(rootEntity)

        if (instance != 0) {
            val transformMatrix = FloatArray(16)
            Matrix.setIdentityM(transformMatrix, 0)

            // Điều chỉnh kích thước của nền để bao trùm vật thể chính
            Matrix.scaleM(transformMatrix, 0, 100.0f, 100.0f, 100.0f)

            // Nếu cần, điều chỉnh vị trí của nền
            // Matrix.translateM(transformMatrix, 0, x, y, z)

            transformManager.setTransform(instance, transformMatrix)
        }
    }


    private fun readAsset(context: Context, fileName: String): ByteArray {
        context.assets.open(fileName).use { input ->
            return input.readBytes()
        }
    }

    fun destroy() {
        if (backgroundAsset != null) {
            assetLoader.destroyAsset(backgroundAsset!!)
            backgroundAsset = null
        }
    }
}
