
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

        parseResource(R.layout.activity_tag_view);
    }

    private void parseResource(int resId) {
        Resources rez = getResources();

        XmlResourceParser xml = rez.getXml(resId);
        while (true) {
            try {
                xml.next();
                switch (xml.getEventType()) {
                    case XmlPullParser.END_DOCUMENT:
                        return;

                    case XmlPullParser.START_DOCUMENT:
                        Log.d("XML", "doc: " + xml.getName());
                        break;

                    case XmlPullParser.START_TAG:
                        Log.d("XML", "tag: " + xml.getName());
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            Log.d("XML", "attr @" + i + ": " + xml.getAttributeName(i) + "=" + xml.getAttributeValue(i));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        Log.d("XML", "text: " + xml.getText());
                }
            }
            catch (Exception e) { return; }
        }
    }
}
