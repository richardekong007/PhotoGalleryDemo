package com.richydave.photogallerydemo;

import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.design.widget.TabLayout;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoGallery extends FragmentActivity implements OnPhotoZoomedListener  {

    private List<Integer> photos;
    private String pageDetail;
    private PhotoViewPagerAdapter adapter;
    private int currentPage = 1;
    @BindView(R.id.swiper)
    PhotoViewPager photoViewPager;
    @BindView(R.id.pagination)
    TextView pageInfo;
    @BindView(R.id.tab_layout)
    TabLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        ButterKnife.bind(this);
        PhotoHolderFragment.setOnPhotoZoomedListener(this);
        init();
    }

    private void init() {
        photos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            photos.add(R.drawable.ic_launcher_background);
        }
        setPageDetail();
        configureViewPager();

    }

    @Override
    public void onPhotoZoomed(boolean zoomed) {
        boolean swipeable = ! zoomed;
        photoViewPager.setSwiped(swipeable);
        Log.d("Zoomed from Activity",String.valueOf(zoomed));
    }

    private void configureViewPager() {
        adapter = new PhotoViewPagerAdapter(getSupportFragmentManager());
        photoViewPager.setAdapter(adapter);
        photoViewPager.setSwiped(true);
        tableLayout.setupWithViewPager(photoViewPager);
        tableLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPage = tab.getPosition() + 1;
                setPageDetail();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setPageDetail() {
        pageDetail = String.format(Locale.US, getString(R.string.pagination_format),
                currentPage, photos.size());
        pageInfo.setText(pageDetail);
    }

    public class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {

        private PhotoHolderFragment fragment;

        private PhotoViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putInt(PhotoHolderFragment.PHOTO, photos.get(position));
            fragment = new PhotoHolderFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageDetail != null ? pageDetail : "";
        }

        public PhotoHolderFragment getFragment() {
            return fragment != null ? fragment : new PhotoHolderFragment();
        }
    }

    public static class PhotoHolderFragment extends Fragment {

        private RectF screenRect;
        private static OnPhotoZoomedListener  onPhotoZoomedListener;
        protected PhotoViewAttacher attacher;
        private static boolean zoomed;
        public final static String PHOTO = "PHOTO";

        @BindView(R.id.photo_holder)
        PhotoView photoView;

        @Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View view = layoutInflater.inflate(R.layout.fragment_photo_holder, viewGroup, false);
            ButterKnife.bind(this, view);
            loadPhoto();
            return view;
        }

        private void loadPhoto() {
            final Bundle receivedPhotoInfo = getArguments() != null ? getArguments() : new Bundle();
            Glide.with(this)
                    .load(receivedPhotoInfo.getInt(PHOTO))
                    .into(photoView);
            attacher = photoView.getAttacher();
            screenRect = new RectF(attacher.getDisplayRect());
            attacher.setOnMatrixChangeListener(new OnMatrixChangedListener() {
                @Override
                public void onMatrixChanged(RectF rect) {
                    float leftDiff, topDiff, rightDiff, bottomDiff;
                    if (screenRect.left == 0 && screenRect.top == 0
                            && screenRect.right == 0 && screenRect.bottom == 0) {
                        screenRect = new RectF(rect);
                    }
                    leftDiff = Math.abs(screenRect.left - rect.left);
                    topDiff = Math.abs(screenRect.top - rect.top);
                    rightDiff = Math.abs(screenRect.right - rect.right);
                    bottomDiff = Math.abs(screenRect.bottom - rect.bottom);

                    if (leftDiff < 2 && topDiff < 2 && rightDiff < 2 && bottomDiff < 2) {
                        setZoomed(false);
                        Log.d("edge points", String.format(Locale.US, "%f %f %f %f", leftDiff, topDiff, rightDiff, bottomDiff));
                    } else {
                        setZoomed(true);
                    }
                    onPhotoZoomedListener.onPhotoZoomed(zoomed);
                }
            });
        }

        private void setZoomed(boolean isZoomed) {
            zoomed = isZoomed;
            Log.d("is Zoomed", String.valueOf(zoomed));
        }

        public static void setOnPhotoZoomedListener(OnPhotoZoomedListener zoomedListener){
            onPhotoZoomedListener = zoomedListener;
        }

    }
}
