package homeautomation.circularblue.com.homeautomation;

/**
 * Created by rohan on 07-05-2017.
 */

public enum ModelObject {

    RED(R.string.red, R.layout.view_page_0),
    BLUE(R.string.blue, R.layout.view_page_1),
    GREEN(R.string.green, R.layout.view_page_2);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}