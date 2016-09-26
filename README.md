# EasyScaleView

An easy-to-use scale view for Android.  
![demo gif](https://github.com/lynnzc/EasyScaleView/blob/master/gif/easyscaleview.gif)  
  
# Usage
use additional attributes in your layout file   

	<com.lynn.code.easyscaleselectorview.HorizontalScaleView  
            android:id="@+id/horizontal_scale"  
            android:layout_width="match_parent"  
            android:layout_height="100dp" />    

initialize the view in java class

	HorizontalScaleView horizontalScaleView = (HorizontalScaleView) findViewById(R.id.horizontal_scale);  
	List<String> values = new ArrayList<>();  
    for (int i = 0; i <= 200; i++) {  
        values.add(i + "");  
    }  
    horizontalScaleView.initValues(values, new EasyBaseScaleView.OnValueSelectedCallback() {  
        @Override  
        public void onValueSelected(String value) {  
            Log.d("horizontal value: ", value);  
        }  
    });  

# Lincense

    Copyright (C) 2016, Lynn
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.