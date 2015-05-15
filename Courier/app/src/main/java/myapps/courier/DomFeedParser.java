package myapps.courier;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

class DomFeedParser {
    final String LOG_TAG = "myLogs";
    private Context c;

    public DomFeedParser(Context c) {
        this.c = c;
    }

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
                                    break;
                                case "time":
                                    order.time = xpp.getText();
                                    break;
                                case "phone":
                                    order.phone = xpp.getText();
                                    break;
                                case "address":
                                    order.address = xpp.getText();
                                    break;
                                case "product":
                                    order.product = xpp.getText();
                                    break;
                                case "comments":
                                    order.comments = xpp.getText();
                                    break;
                            }
                        } catch (Exception ex) {

                        }
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
          //  Log.d(LOG_TAG, "END_DOCUMENT");

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
