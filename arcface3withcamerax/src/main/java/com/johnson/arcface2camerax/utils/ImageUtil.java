package com.johnson.arcface2camerax.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtil {
    private static final int VALUE_FOR_4_ALIGN = 0b11;
    private static final int VALUE_FOR_2_ALIGN = 0b01;
    public static final int YUV420P = 0;
    public static final int YUV420SP = 1;
    public static final int NV21 = 2;
    public static final int Degree90 = 3;       //旋转90
    private static final String TAG = "ImageUtil";
    private static byte uBytes[];
    private static byte vBytes[];
    private static byte[] yuvBytes;
    private static byte[] tempBytes;


    /**
     * Bitmap转化为ARGB数据，再转化为NV21数据
     *
     * @param src 传入的Bitmap，格式为{@link Bitmap.Config#ARGB_8888}
     * @return nv21数据
     */
    public static byte[] bitmapToNv21(Bitmap src) {
        if (src != null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] argb = new int[width * height];
            src.getPixels(argb, 0, width, 0, 0, width, height);
            return argbToNv21(argb, width, height);
        } else {
            return null;
        }
    }

    /**
     * ARGB数据转化为NV21数据
     *
     * @param argb   argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    private static byte[] argbToNv21(int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int R = (argb[index] & 0xFF0000) >> 16;
                int G = (argb[index] & 0x00FF00) >> 8;
                int B = argb[index] & 0x0000FF;
                int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                nv21[yIndex++] = (byte) (Y < 0 ? 0 : (Y > 255 ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (V < 0 ? 0 : (V > 255 ? 255 : V));
                    nv21[uvIndex++] = (byte) (U < 0 ? 0 : (U > 255 ? 255 : U));
                }

                ++index;
            }
        }
        return nv21;
    }

    /**
     * bitmap转化为bgr数据，格式为{@link Bitmap.Config#ARGB_8888}
     *
     * @param image 传入的bitmap
     * @return bgr数据
     */
    public static byte[] bitmapToBgr(Bitmap image) {
        if (image == null) {
            return null;
        }
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] temp = buffer.array();
        byte[] pixels = new byte[(temp.length / 4) * 3];
        for (int i = 0; i < temp.length / 4; i++) {
            pixels[i * 3] = temp[i * 4 + 2];
            pixels[i * 3 + 1] = temp[i * 4 + 1];
            pixels[i * 3 + 2] = temp[i * 4];
        }
        return pixels;
    }

    /**
     * 裁剪bitmap
     *
     * @param bitmap 传入的bitmap
     * @param rect   需要被裁剪的区域
     * @return 被裁剪后的bitmap
     */
    public static Bitmap imageCrop(Bitmap bitmap, Rect rect) {
        if (bitmap == null || rect == null || rect.isEmpty() || bitmap.getWidth() < rect.right || bitmap.getHeight() < rect.bottom) {
            return null;
        }
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), null, false);
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        if (uri == null || context == null) {
            return null;
        }
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        if (b == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }

    /**
     * 确保传给引擎的BGR24数据宽度为4的倍数
     *
     * @param bitmap 传入的bitmap
     * @return 调整后的bitmap
     */
    public static Bitmap alignBitmapForBgr24(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() < 4) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        boolean needAdjust = false;

        //保证宽度是4的倍数
        if ((width & VALUE_FOR_4_ALIGN) != 0) {
            width &= ~VALUE_FOR_4_ALIGN;
            needAdjust = true;
        }

        if (needAdjust) {
            bitmap = imageCrop(bitmap, new Rect(0, 0, width, height));
        }
        return bitmap;
    }

    /**
     * 确保传给引擎的NV21数据宽度为4的倍数，高为2的倍数
     *
     * @param bitmap 传入的bitmap
     * @return 调整后的bitmap
     */
    public static Bitmap alignBitmapForNv21(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() < 4 || bitmap.getHeight() < 2) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        boolean needAdjust = false;
        //保证宽度是4的倍数
        if ((width & VALUE_FOR_4_ALIGN) != 0) {
            width &= ~VALUE_FOR_4_ALIGN;
            needAdjust = true;
        }

        //保证高度是2的倍数
        if ((height & VALUE_FOR_2_ALIGN) != 0) {
            height--;
            needAdjust = true;
        }

        if (needAdjust) {
            bitmap = imageCrop(bitmap, new Rect(0, 0, width, height));
        }
        return bitmap;
    }

    /***
     * 此方法内注释以640*480为例
     * 未考虑CropRect的
     */
    public static byte[] getBytesFromImageAsType(Image image, int type) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width;
            int height;

           /* if (degree == Degree90) {
                height = image.getWidth();
                width = image.getHeight();
            } else {*/
            width = image.getWidth();
            height = image.getHeight();
//            }


            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

            //临时存储uv数据的
            uBytes = new byte[width * height / 4];
            vBytes = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                //如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                tempBytes = new byte[buffer.capacity()];
                buffer.get(tempBytes);

                /*if (degree == Degree90){
                    bytes = rotateYUVDegree90(bytes,height,width);
                }*/

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(tempBytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = tempBytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = tempBytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
            }

            image.close();

            //根据要求的结果类型进行填充
            switch (type) {
                case YUV420P:
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }
            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
        return null;
    }


    private byte[] rotateYUVDegree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }


    public static Bitmap createPhotos90(Bitmap bitmap) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            try {
                m.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);//90就是我们需要选择的90度
                Bitmap bmp2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                bitmap.recycle();
                bitmap = bmp2;
            } catch (Exception ex) {
                System.out.print("创建图片失败！" + ex);
            }
        }
        return bitmap;
    }


}
