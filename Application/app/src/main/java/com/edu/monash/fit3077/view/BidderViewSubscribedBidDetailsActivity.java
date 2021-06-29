package com.edu.monash.fit3077.view;

import android.os.Handler;
import android.os.Looper;

public class BidderViewSubscribedBidDetailsActivity extends BidderViewBidDetailsActivity{
    private final int INTERVAL = 10000;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;

    @Override
    protected void onPostResume() {
        // refresh the page every 10 seconds
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, INTERVAL);
                bidDetailsViewModel.getBidRequestDetails(selectedBidRequest.getId());
            }
        }, INTERVAL);

        super.onPostResume();
    }

    @Override
    public void onPause() {
        // stop refreshing every 10 seconds when user leave the page
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}
