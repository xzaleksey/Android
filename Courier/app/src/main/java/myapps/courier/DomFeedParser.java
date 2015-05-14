package myapps.courier;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

class DomFeedParser {
    private Context c;

    public DomFeedParser(Context c) {
        this.c = c;
    }

    final String LOG_TAG = "myLogs";

    void parseXml2(List<Order> orders) {
        String tmp = "";
        // final int INDEX = 1,NAME = 2,TIME=3,PHONE=4,ADDRESS=5,PRODUCT=6,COMMENTS=7;
        try {
            XmlPullParser xpp = prepareXpp();
            Order order = null;
            String tag = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    // case XmlPullParser.START_DOCUMENT:
                    //     break;
                    case XmlPullParser.START_TAG:
                        if (xpp.getDepth() == 2) {
                            order = new Order();
                            orders.add(order);
                        } else if (xpp.getDepth() == 3) {
                            tag = xpp.getName();
                        }
                        break;
//                    case XmlPullParser.END_TAG:
//                        if(xpp.getDepth()==2){
//
//                        }
//                        // Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
//                        break;
                    case XmlPullParser.TEXT:
                        try {
                            switch (tag) {
                                case "index":
                                    order.index = Integer.parseInt(xpp.getText());
                                    break;
                                case "name":
                                    order.name = xpp.getText();
                                case "time":
                                    order.time = xpp.getText();
                                case "phone":
                                    order.phone = xpp.getText();
                                case "address":
                                    order.address = xpp.getText();
                                case "product":
                                    order.product = xpp.getText();
                                case "comments":
                                    order.comments = xpp.getText();
                            }
                        } catch (Exception ex) {

                        }
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
            Log.d(LOG_TAG, "END_DOCUMENT");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void parseXml() {
        String tmp = "";
        try {
            XmlPullParser xpp = prepareXpp();

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(LOG_TAG, "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        Log.d(LOG_TAG, "START_TAG: name = " + xpp.getName()
                                + ", depth = " + xpp.getDepth() + ", attrCount = "
                                + xpp.getAttributeCount());
                        tmp = "";
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            tmp = tmp + xpp.getAttributeName(i) + " = "
                                    + xpp.getAttributeValue(i) + ", ";
                        }
                        if (!TextUtils.isEmpty(tmp))
                            Log.d(LOG_TAG, "Attributes: " + tmp);
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
                        break;
                    case XmlPullParser.TEXT:
                        Log.d(LOG_TAG, "text = " + xpp.getText());
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
            Log.d(LOG_TAG, "END_DOCUMENT");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    XmlPullParser prepareXpp() {
        return c.getResources().getXml(R.xml.work);
    }
}
