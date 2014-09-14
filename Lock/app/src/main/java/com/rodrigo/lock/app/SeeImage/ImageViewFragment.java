package com.rodrigo.lock.app.SeeImage;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rodrigo.lock.app.Core.crypto.DecryptControllerSeeImage;
import com.rodrigo.lock.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Rodrigo on 08/08/2014.
 */
public class ImageViewFragment extends Fragment {
    @InjectView(R.id.iv_photo) PhotoView photoView;
    @InjectView(R.id.progress) View progreso;

    private DecryptControllerSeeImage controller;
    private int imageid;
    private  PhotoViewAttacher mAttacher = null;

    public ImageViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_imagesegureview, container, false);
        ButterKnife.inject(this, V);

        imageid=getArguments().getInt("imageID");

        SetBitmapTask task  = new SetBitmapTask();
        task.execute(imageid);

        return V;
    }



    private class SetBitmapTask extends AsyncTask<Integer, Void, Bitmap> {

        public SetBitmapTask( ){
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap image = ((SeeImageActivity) getActivity()).getControllerImage().getImage(imageid);
            return  image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progreso.setVisibility(View.GONE);
            photoView.setImageBitmap(result);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mAttacher  = new PhotoViewAttacher(photoView);
                mAttacher.setOnPhotoTapListener(new PhotoTapListener());
            }

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // photoView.destroyDrawingCache();
       // photoView.setImageBitmap(null);
        if (mAttacher!= null)
            mAttacher.cleanup();
    }



    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {
        @Override
        public void onPhotoTap(View view, float x, float y) {
            SeeImageActivity padre = (SeeImageActivity) getActivity();
            padre.regularBar();
        }
    }



}