package sid.comslav.com.circleofmusic.helpers;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class verticalSpaceDecorationHelper extends RecyclerView.ItemDecoration {
    private final int mVerticalSpaceHeight;

    public verticalSpaceDecorationHelper(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mVerticalSpaceHeight;
    }
}
