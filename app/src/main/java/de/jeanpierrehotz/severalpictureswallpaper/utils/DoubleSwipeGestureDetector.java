/*
 *     Copyright 2017 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jeanpierrehotz.severalpictureswallpaper.utils;

import android.view.MotionEvent;

/**
 *
 */
public class DoubleSwipeGestureDetector {

    private Vector2D mFirst0;
    private Vector2D mFirst1;
    private Vector2D mCurrDelta0;
    private Vector2D mCurrDelta1;
    private Vector2D mPrev0;
    private Vector2D mPrev1;
    private Vector2D mPrevDelta0;
    private Vector2D mPrevDelta1;

    private OnDoubleDownSwipeListener mOnDoubleDownSwipeListener;
    private OnDoubleLeftSwipeListener mOnDoubleLeftSwipeListener;
    private OnDoubleRightSwipeListener mOnDoubleRightSwipeListener;
    private OnDoubleUpSwipeListener mOnDoubleUpSwipeListener;

    private boolean valid;
    private boolean validInX;
    private boolean validInY;
    private boolean mDoubleDown;
    private boolean mEventFired;

    public void removeOnDoubleDownSwipeListener() {
        this.mOnDoubleDownSwipeListener = null;
    }

    public void setOnDoubleDownSwipeListener(OnDoubleDownSwipeListener mOnDoubleDownSwipeListener) {
        this.mOnDoubleDownSwipeListener = mOnDoubleDownSwipeListener;
    }

    public void removeOnDoubleUpSwipeListener() {
        this.mOnDoubleUpSwipeListener = null;
    }

    public void setOnDoubleUpSwipeListener(OnDoubleUpSwipeListener mOnDoubleUpSwipeListener) {
        this.mOnDoubleUpSwipeListener = mOnDoubleUpSwipeListener;
    }

    public void setOnDoubleLeftSwipeListener(OnDoubleLeftSwipeListener listener) {
        this.mOnDoubleLeftSwipeListener = listener;
    }

    public void removeOnDoubleLeftSwipeListener() {
        this.mOnDoubleLeftSwipeListener = null;
    }

    public void setOnDoubleRightSwipeListener(OnDoubleRightSwipeListener listener) {
        this.mOnDoubleRightSwipeListener = listener;
    }

    public void removeOnDoubleRightSwipeListener() {
        this.mOnDoubleRightSwipeListener = null;
    }

    public DoubleSwipeGestureDetector() {
        this.mDoubleDown = false;
        this.mEventFired = false;
        this.valid = true;
        this.validInX = true;
        this.validInY = true;
        this.mPrev0 = new Vector2D();
        this.mPrev1 = new Vector2D();
        this.mFirst0 = new Vector2D();
        this.mFirst1 = new Vector2D();
        this.mPrevDelta0 = new Vector2D();
        this.mPrevDelta1 = new Vector2D();
        this.mCurrDelta0 = new Vector2D();
        this.mCurrDelta1 = new Vector2D();
    }

    private void fireLeftSwipe() {
        if (!this.mEventFired && this.mOnDoubleLeftSwipeListener != null) {
            this.mEventFired = true;
            this.mOnDoubleLeftSwipeListener.onDoubleLeftSwiped();
        }
    }

    private void fireRightSwipe() {
        if (!this.mEventFired && this.mOnDoubleRightSwipeListener != null) {
            this.mEventFired = true;
            this.mOnDoubleRightSwipeListener.onDoubleRightSwiped();
        }
    }

    private void fireUpSwipe() {
        if (!this.mEventFired && this.mOnDoubleUpSwipeListener != null) {
            this.mEventFired = true;
            this.mOnDoubleUpSwipeListener.onDoubleUpSwiped();
        }
    }

    private void fireDownSwipe() {
        if (!this.mEventFired && this.mOnDoubleDownSwipeListener != null) {
            this.mEventFired = true;
            this.mOnDoubleDownSwipeListener.onDoubleDownSwiped();
        }
    }

    public void onTouchEvent(MotionEvent evt) {
        this.mDoubleDown = evt.getPointerCount() == 2;

        if (!this.mDoubleDown) {

            this.mEventFired = evt.getPointerCount() > 2;
            this.valid = evt.getPointerCount() < 2;

        } else if (evt.getAction() == MotionEvent.ACTION_DOWN || evt.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {

            this.mFirst0.setPosition(evt.getX(0), evt.getY(0));
            this.mFirst1.setPosition(evt.getX(1), evt.getY(1));
            this.mPrev0.setPosition(evt.getX(0), evt.getY(0));
            this.mPrev1.setPosition(evt.getX(1), evt.getY(1));
            this.mPrevDelta0.setPosition(0.0f, 0.0f);
            this.mPrevDelta1.setPosition(0.0f, 0.0f);

        } else if (evt.getAction() == MotionEvent.ACTION_UP || evt.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {

            if (this.valid) {

                float deltaX = ((this.mFirst0.getX() - evt.getX(0)) + (this.mFirst1.getX() - evt.getX(1))) / 2.0f;
                float deltaY = ((this.mFirst0.getY() - evt.getY(0)) + (this.mFirst1.getY() - evt.getY(1))) / 2.0f;

                if (this.validInX != this.validInY) {
                    if (this.validInX) {
                        if (deltaX > 0.0f) {
                            fireLeftSwipe();
                        } else {
                            fireRightSwipe();
                        }
                    } else if (deltaY > 0.0f) {
                        fireUpSwipe();
                    } else {
                        fireDownSwipe();
                    }
                } else if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0.0f) {
                        fireLeftSwipe();
                    } else {
                        fireRightSwipe();
                    }
                } else if (deltaY > 0.0f) {
                    fireUpSwipe();
                } else {
                    fireDownSwipe();
                }
            } else {
                this.valid = true;
            }

            this.validInX = true;
            this.validInY = true;

        } else if (this.valid) {

            this.mCurrDelta0.setPosition(Math.abs(evt.getX(0) - this.mFirst0.getX()), Math.abs(evt.getY(0) - this.mFirst0.getY()));
            this.mCurrDelta1.setPosition(Math.abs(evt.getX(1) - this.mFirst1.getX()), Math.abs(evt.getY(1) - this.mFirst1.getY()));

            if (this.validInX && (this.mCurrDelta0.getX() < this.mPrevDelta0.getX() || this.mCurrDelta1.getX() < this.mPrevDelta1.getX())) {
                this.validInX = false;
            }

            if (this.validInY && (this.mCurrDelta0.getY() < this.mPrevDelta0.getY() || this.mCurrDelta1.getY() < this.mPrevDelta1.getY())) {
                this.validInY = false;
            }

            this.mPrevDelta0.setPosition(Math.abs(evt.getX(0) - this.mFirst0.getX()), Math.abs(evt.getY(0) - this.mFirst0.getY()));
            this.mPrevDelta1.setPosition(Math.abs(evt.getX(1) - this.mFirst1.getX()), Math.abs(evt.getY(1) - this.mFirst1.getY()));

            if (!this.validInX && !this.validInY) {
                this.valid = false;
            }
        }
    }

    private static class Vector2D {
        private float x;
        private float y;

        public Vector2D() {
            this.x = 0.0f;
            this.y = 0.0f;
        }

        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }
    }

    public interface OnDoubleDownSwipeListener {
        void onDoubleDownSwiped();
    }

    public interface OnDoubleLeftSwipeListener {
        void onDoubleLeftSwiped();
    }

    public interface OnDoubleRightSwipeListener {
        void onDoubleRightSwiped();
    }

    public interface OnDoubleUpSwipeListener {
        void onDoubleUpSwiped();
    }
}
