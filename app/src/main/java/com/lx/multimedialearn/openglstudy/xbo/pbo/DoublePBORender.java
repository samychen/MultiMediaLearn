package com.lx.multimedialearn.openglstudy.xbo.pbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.lx.multimedialearn.R;
import com.lx.multimedialearn.openglstudy.OpenGLJniUtils;
import com.lx.multimedialearn.utils.FileUtils;
import com.lx.multimedialearn.utils.GlUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samychen on 2018/5/30.
 */

public class DoublePBORender implements GLSurfaceView.Renderer {
    private Context mContext;
    private int mWidth;
    private int mheight;

    public DoublePBORender(Context context) {
        this.mContext = context;
    }

    private int mProgram;
    private int mVertexLocation;
    private int mCoordLocation;
    private int mMatrixLocation;
    private int mTextureLocation;
    private int mTextureID;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mCoordBuffer;
    private float[] mVertices = {
            -1.0f, -1.0f, //左下
            1.0f, -1.0f, //右下
            -1.0f, 1.0f, //左上
            1.0f, 1.0f, //右上
    };
    private float[] mTextureCoords = {
            0.0f, 1.0f, //左下
            1.0f, 1.0f, //右下
            0.0f, 0.0f, //左上
            1.0f, 0.0f, //右上
    };
    private float[] mMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    private int mVBOVertexBuffer; //使用VBO
    private int[] mPBOVertexBuffer; //使用PBO

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //加载Program，生成纹理，加载图片，查找属性位置
        String vertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.last_vertex_shader);
        String vragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.last_fragment_shader);
        mProgram = GlUtil.createProgram(vertexShader, vragmentShader);
        mVertexLocation = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mCoordLocation = GLES20.glGetAttribLocation(mProgram, "aCoord");
        mMatrixLocation = GLES20.glGetUniformLocation(mProgram, "uMatrix");
        mTextureLocation = GLES20.glGetUniformLocation(mProgram, "uTexture");

        int[] buffers = new int[1];
        GLES20.glGenBuffers(buffers.length, buffers, 0); //生成bufferid
        mVBOVertexBuffer = buffers[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOVertexBuffer);
        mVertexBuffer = GlUtil.createFloatBuffer(mVertices); //把数据存储到本地（java空间访问不到）
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * GlUtil.SIZEOF_FLOAT, mVertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mCoordBuffer = GlUtil.createFloatBuffer(mTextureCoords);

        //加载一张图片，创建纹理，加载图片，返回id
        int[] temp = GlUtil.createImageTexture(mContext, R.drawable.q); //传统的使用texImage2D加载，占用cpu时间
        mTextureID = temp[0];
    }

    int width;
    int height;
    int index;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height); //图像要放的位置
        this.width = width;
        this.height = height;
        createPbo();
    }
    int mPixelStride = 4;
    int mRowStride;
    int mPboSize ;
    int pboIndex;
    ByteBuffer bf;
    private final String TAG = "DoublePBORender";
    public void createPbo(){
        Log.e("draw", "createPbo: " );
        //创建pbo，绑定pbo，读取数据到pbo，映射到内存，展示到ImageView上
        //初始化宽高，考虑字节对齐
        //OpenGLES默认应该是4字节对齐应，但是不知道为什么在索尼Z2上效率反而降低
        //并且跟ImageReader最终计算出来的rowStride也和我这样计算出来的不一样，这里怀疑跟硬件和分辨率有关
        int align = 4;//32位的cpu4字节对齐，64的cpu应该是8字节对齐，但是在索尼z2上128字节对齐才没有降低效率
        mRowStride = (width * mPixelStride + (align - 1)) & ~(align - 1); //字节对齐
        mPBOVertexBuffer = new int[2];
        mPboSize = mRowStride * height;
        Log.e(TAG, "createPbo: width="+width+"height="+height+"mRowStride="+mRowStride );
        GLES30.glGenBuffers(2, mPBOVertexBuffer, 0);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPBOVertexBuffer[0]);
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, mPboSize, null, GLES30.GL_STATIC_READ);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPBOVertexBuffer[1]);
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, mPboSize, null, GLES30.GL_STATIC_READ);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void bindPixbuffer(){
        pboIndex++;
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPBOVertexBuffer[pboIndex%2]);
        //使用GLES20不能读取到PBO，GL30需要target>24，使用Jni调用本地方法，调用Opengl本地方法，图片没有a通道，rgba到argb需要转换
        OpenGLJniUtils.glReadPixels(0, 0, mRowStride / mPixelStride, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,mPBOVertexBuffer[(pboIndex+1)%2]);
        bf = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_PIXEL_PACK_BUFFER, 0, mPboSize, GLES30.GL_MAP_READ_BIT);
        GLES30.glUnmapBuffer(GLES30.GL_PIXEL_PACK_BUFFER);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //设置属性值
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrix, 0);
        GLES20.glEnableVertexAttribArray(mVertexLocation);
        //使用vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOVertexBuffer); //绑定id，表示要使用
        GLES20.glEnableVertexAttribArray(mVertexLocation); //
        GLES20.glVertexAttribPointer(mVertexLocation, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0); //解绑，同理使用索引，drawElements也有对应的索引Buffer

        GLES20.glEnableVertexAttribArray(mCoordLocation);
        GLES20.glVertexAttribPointer(mCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mCoordBuffer);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //把活动的纹理单元设置为纹理单元0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID); //把纹理绑定到纹理单元0上
        GLES20.glUniform1i(mTextureLocation, 0); //把纹理单元0传给片元着色器进行渲染
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); //数据处理完后，使用readPiex
        Log.e("draw", "onDrawFrame: " );
        // update
        bindPixbuffer();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(bf);
        if (mListener != null) {
            mListener.update(bitmap);
        }
    }

    /**
     * 读取出来为倒置的BGRA，需要转换为ARGB
     * 转换成Bitmap演示用效率低下，可以用libyuv代替
     *
     * @param buffer
     * @return
     */
    private int[] bgra2argb(ByteBuffer buffer, int width, int height, int pixelStride, int rowStride) {
        byte[] data = new byte[rowStride * height]; //buffer实际大小是这两个的乘积
        buffer.get(data);
        int[] resultData = new int[width * height];
        int offset = 0;
        int index = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int pixel = 0;
                pixel |= (data[offset] & 0xff) << 16;     // R
                pixel |= (data[offset + 1] & 0xff) << 8;  // G
                pixel |= (data[offset + 2] & 0xff);       // B
                pixel |= (data[offset + 3] & 0xff) << 24; // A
                resultData[index++] = pixel;
                offset += 4;
            }
            offset += rowStride - width * pixelStride;
        }

//        ByteBuffer bf = ByteBuffer.allocateDirect(width * height * 4);
//        IntBuffer intBuffer = bf.asIntBuffer();
//        intBuffer.put(resultData);
        return resultData;
    }

    private PBORender.onBitmapUpdateListener mListener;

    public void setListener(PBORender.onBitmapUpdateListener listener) {
        this.mListener = listener;
    }

    /**
     * 图片生成后回调
     */
    public interface onBitmapUpdateListener {
        void update(Bitmap bitmap);
    }
}
