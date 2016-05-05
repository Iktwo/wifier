package com.iktwo.wifier.networks;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.iktwo.wifier.Injection;
import com.iktwo.wifier.R;

public class NetworksActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = NetworksActivity.class.getSimpleName();
    private BroadcastReceiver mBroadcastReceiverNetworks;
    private NetworksPresenter mNetworksPresenter;

    @Override
    protected void onPause() {
        super.onPause();

        if (mBroadcastReceiverNetworks != null) {
            unregisterReceiver(mBroadcastReceiverNetworks);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBroadcastReceiverNetworks == null) {
            mBroadcastReceiverNetworks = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Log.d(TAG, "onReceive " + WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                    if (mNetworksPresenter != null) {
                        mNetworksPresenter.loadNetworks(false);
                    }
                }
            };
        }

        registerReceiver(mBroadcastReceiverNetworks, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.networks_act);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //noinspection ConstantConditions
        toolbar.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        toolbar.getViewTreeObserver().removeOnPreDrawListener(this);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

                        toolbar.measure(widthSpec, heightSpec);
                        collapseToolbar(toolbar);
                        return true;
                    }
                });

        setSupportActionBar(toolbar);

        NetworksFragment networksFragment =
                (NetworksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (networksFragment == null) {
            networksFragment = NetworksFragment.newInstance();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, networksFragment);
            transaction.commit();
        }

        mNetworksPresenter = new NetworksPresenter(
                Injection.provideNetworksRepository(getApplicationContext()), networksFragment);

        /// TODO: ask if wifi is enable, if not, ask if user wants to enable it
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void collapseToolbar(final Toolbar toolbar) {
        int toolBarHeight;
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        toolBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        ValueAnimator valueHeightAnimator = ValueAnimator.ofInt(toolbar.getHeight(), toolBarHeight);
        valueHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                lp.height = (Integer) animation.getAnimatedValue();
                toolbar.setLayoutParams(lp);
            }
        });

        valueHeightAnimator.start();
        valueHeightAnimator.addListener(new AnimatorListenerAdapter() {
        });
    }
}
