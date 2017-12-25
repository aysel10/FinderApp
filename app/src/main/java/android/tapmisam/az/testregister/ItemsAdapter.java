package android.tapmisam.az.testregister;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Created by ayselkas on 12/5/17.
 */
/**
 * Класс адаптера наследуется от RecyclerView.Adapter с указанием класса,
 * который будет хранить ссылки на виджеты элемента списка, т.е. класса, имплементирующего ViewHolder.
 * В нашем случае класс объявлен внутри класса адаптера.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {
    private Context mContext;
    private List<Item> items;
    private int width;
    private int height;
    private boolean isLoadingAdded = false;

    public ItemsAdapter(Context mContext, List<Item> items) {
        this.mContext = mContext;
        this.items = items;
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImage;
        private TextView title, description;

        public MyViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.title);
            description= view.findViewById(R.id.description);
            itemImage=view.findViewById(R.id.thumbnail);
            width=itemImage.getWidth();
            height=itemImage.getHeight();

        }
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */

    @Override
    public ItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false); // который умеет из содержимого layout-файла создать View-элемент
        return new MyViewHolder(itemView);
    }
    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ItemsAdapter.MyViewHolder holder, int position) {

        Item item=items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        Glide.with(mContext).load(item.getImagePath()).centerCrop().into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return items.size(); //вот ошибка
    }

    @Override
    public String toString() {
        return items.toString();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
