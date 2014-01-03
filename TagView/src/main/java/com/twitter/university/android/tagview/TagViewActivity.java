
package com.twitter.university.android.tagview;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.twitter.university.android.tagview.widget.TagView;

import org.xmlpull.v1.XmlPullParser;


public class TagViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_view);

        TagView tags = ((TagView) findViewById(R.id.tags));
        tags.addTag("One", 0);
        tags.addTag("Two", 0);
        tags.addTag("Three", 0);
        tags.addTag("Four", 0);
        tags.addTag("Five", 0);
        tags.addTag("Six", 1);
        tags.addTag("Seven", 0);
        tags.addTag("Eight", 0);
        tags.addTag("Nine", 1);
        tags.addTag("Ten", 0);
        tags.addTag("Eleven", 0);
        tags.addTag("Twelve", 0);
        tags.addTag("Thirteen Twenty-three Thirty-three Forty-three Fifty-three Sixty-three Seventy-three Eighty-three Ninty-three One oh three", 0);
        tags.addTag("Fourteen", 0);
        tags.addTag("Fifteen", 0);
        tags.addTag("Sixteen", 0);
    }
}
