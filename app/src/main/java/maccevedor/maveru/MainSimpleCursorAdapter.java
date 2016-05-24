package maccevedor.maveru;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by mauricio.acevedo on 24/05/16.
 */
public class MainSimpleCursorAdapter extends SimpleCursorAdapter {

    public MainSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null){
            holder = new ViewHolder();
            holder.colImp = cursor.getColumnIndexOrThrow(MainDBAdapter.COL_IMPORTANT);
            holder.listTab = view.findViewById(R.id.row_tab);
            view.setTag(holder);
        }

        if  (cursor.getInt(holder.colImp) > 0) {
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.naranja));
        }else{
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.azul_claro));
        }
    }

    static class ViewHolder {
        int colImp;
        View listTab;
    }
}
