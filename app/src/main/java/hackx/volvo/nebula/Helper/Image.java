package hackx.volvo.nebula.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.widget.ImageView;

public class Image {
    private static final Image ourInstance = new Image();

    public static Image getInstance() {
        return ourInstance;
    }

    private Image(){
    }

    public static Bitmap GetRotatedImage(String imageLocation){
        Bitmap myBitmap = BitmapFactory.decodeFile(imageLocation);
        return rotate(myBitmap, getRotationValue(imageLocation));

    }

    public static RectF getImageBounds(ImageView imageView) {
        RectF bounds = new RectF();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            imageView.getImageMatrix().mapRect(bounds, new RectF(drawable.getBounds()));
        }
        return bounds;
    }
    private static int getRotationValue(String imageLocation){
        try
        {
            ExifInterface exif = new ExifInterface(imageLocation);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return exifToDegrees(rotation);
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    private static  int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
