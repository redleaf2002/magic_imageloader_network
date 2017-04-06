/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.leaf.magic.image.listener;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ListView;

import com.leaf.magic.Magic;

/**
 * Listener-helper for {@linkplain AbsListView list views} ({@link ListView}, {@link GridView}) which can
 * pause Magic's tasks}while list view is scrolling (touch scrolling and/or
 * fling). It prevents redundant loadings.<br />
 * Set it to your list view's {@link AbsListView#setOnScrollListener(OnScrollListener) setOnScrollListener(...)}.<br />
 * This listener can wrap your custom {@linkplain OnScrollListener listener}.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.7.0
 */
public class PauseOnScrollListener implements OnScrollListener {

    private Magic magic;

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final OnScrollListener externalListener;

    public PauseOnScrollListener(Context mContext) {
        this(mContext, true, true, null);
    }

    /**
     * Constructor
     *
     * @param pauseOnScroll Whether pause  during touch scrolling
     * @param pauseOnFling  Whether pause during fling
     */
    public PauseOnScrollListener(Context mContext, boolean pauseOnScroll, boolean pauseOnFling) {
        this(mContext, pauseOnScroll, pauseOnFling, null);
    }

    /**
     * Constructor
     *
     * @param pauseOnScroll  Whether  pause during touch scrolling
     * @param pauseOnFling   Whether  pause  during fling
     * @param customListener Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also
     *                       will be get scroll events
     */
    public PauseOnScrollListener(Context mContext, boolean pauseOnScroll, boolean pauseOnFling,
                                 OnScrollListener customListener) {
        this.magic = Magic.with(mContext);
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                magic.magicEngine.resume();
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                if (pauseOnScroll) {
                    magic.magicEngine.pause();
                }
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                if (pauseOnFling) {
                    magic.magicEngine.pause();
                }
                break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
