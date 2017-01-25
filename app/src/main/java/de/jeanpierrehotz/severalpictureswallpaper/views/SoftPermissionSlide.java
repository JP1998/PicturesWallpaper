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

package de.jeanpierrehotz.severalpictureswallpaper.views;

import android.support.annotation.ColorRes;

import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by Jonny on 25.01.2017.
 */
public class SoftPermissionSlide extends SimpleSlide {

    private final boolean canGoForward;

    protected boolean enforcePermissions;

    protected SoftPermissionSlide(Builder builder) {
        super(builder);

        this.canGoForward = builder.canGoForward;

        this.enforcePermissions = builder.enforcePermissions;
    }

    @Override
    public boolean canGoForward() {
        boolean goForwardEnforcingPermissions = super.canGoForward();

        if(enforcePermissions){
            return goForwardEnforcingPermissions;
        } else {
            return canGoForward;
        }
    }

    public static class Builder extends SimpleSlide.Builder {
        @ColorRes
        private int backgroundRes = 0;

        private boolean canGoForward = true;
        protected boolean enforcePermissions = false;

        public Builder enforcePermissions(boolean enforcePermissions){
            this.enforcePermissions = enforcePermissions;
            return this;
        }

        @Override
        public Builder background(@ColorRes int backgroundRes) {
            super.background(backgroundRes);

            this.backgroundRes = backgroundRes;
            return this;
        }

        @Override
        public Builder canGoForward(boolean canGoForward) {
            super.canGoForward(canGoForward);

            this.canGoForward = canGoForward;
            return this;
        }

        public SoftPermissionSlide build(){
            if (backgroundRes == 0)
                throw new IllegalArgumentException("You must set a background.");

            return new SoftPermissionSlide(this);
        }
    }

}
