package br.ufba.exerciserecognition.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufba.exerciserecognition.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends BaseFragment {

    public SplashFragment() {

    }

    private TextView titleTX;
    private ImageView logoIMG;
    private Handler myHandler;

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBaseActivity().actualFragment = this;
        init(view);
        myHandler = new Handler();
        delay();
    }

    private void delay() {
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (getActivity() == null)
                    return;

                    getBaseActivity().changeFragment(MainFragment.newInstance());

            }

        }, 2000);

    }
    private void init(View view) {
        titleTX = (TextView)view.findViewById(R.id.titleTX);
        logoIMG = (ImageView)view.findViewById(R.id.logoIMG);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AndroidScratch.ttf");
        titleTX.setTypeface(font);

        // Load the animation like this
        Animation animSlide = AnimationUtils.loadAnimation(getActivity(),
                R.anim.left_to_right);

        // Start the animation like this
        logoIMG.startAnimation(animSlide);

    }
}
