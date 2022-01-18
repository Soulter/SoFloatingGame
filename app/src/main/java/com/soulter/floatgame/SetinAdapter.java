package com.soulter.floatgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */
public class SetinAdapter extends ArrayAdapter<SetinItem> {

    private int resourceId;
    public SetinAdapter(Context context, int textViewResourceId,
                        List<SetinItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SetinItem setinItem = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.setinName = (TextView) view.findViewById(R.id.setin_name);
            viewHolder.setinInfo =(TextView) view.findViewById(R.id.setin_info);
            view.setTag(viewHolder);// 将ViewHolder存储在View中。
        }else
        {
            view = convertView;
            viewHolder=(ViewHolder)view.getTag(); //重新获取ViewHolder

        }
        viewHolder.setinName.setText(setinItem.getName());
        viewHolder.setinInfo.setText(setinItem.getInfo());
        return view;
    }

    class ViewHolder{
        TextView setinName;
        TextView setinInfo;
    }

}
