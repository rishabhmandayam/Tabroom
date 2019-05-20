package rm.tabroomfinal;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListAdapter extends BaseAdapter {

    MainActivity main;
    Pattern pattern3 = Pattern.compile("[0-9a-zA-z]+");

    ListAdapter(MainActivity main)
    {
        this.main = main;
    }

    @Override
    public int getCount() {
        return  main.tournamentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolderItem {
        TextView name;
        TextView date;
        TextView code;
        String link;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolderItem holder = new ViewHolderItem();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell, null);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.name.setMovementMethod(LinkMovementMethod.getInstance());
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.code = (TextView) convertView.findViewById(R.id.code);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolderItem) convertView.getTag();
        }


        holder.name.setText(this.main.tournamentList.get(position).getTorunamentName());
        holder.link = this.main.tournamentList.get(position).getTournamentLink();
        holder.name.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(holder.name,pattern3,holder.link,null,myTransformFilter);
        holder.date.setText(this.main.tournamentList.get(position).getTornamentDate());
        holder.code.setText(this.main.tournamentList.get(position).getLocale());

        return convertView;
    }

    Linkify.TransformFilter myTransformFilter = new Linkify.TransformFilter() {
        @Override
        public String transformUrl(Matcher matcher, String url) {
            return url.substring(1);
        }
    };


}
