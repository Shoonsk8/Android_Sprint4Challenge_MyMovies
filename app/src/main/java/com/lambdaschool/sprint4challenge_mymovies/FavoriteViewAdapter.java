package com.lambdaschool.sprint4challenge_mymovies;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lambdaschool.sprint4challenge_mymovies.SQL.FavoriteMovieSQLDAO;
import com.lambdaschool.sprint4challenge_mymovies.apiaccess.MovieApiDao;

public class FavoriteViewAdapter extends RecyclerView.Adapter<FavoriteViewAdapter.ViewHolder>{
    static float xx;

    private FavoriteViewAdapter.ViewHolder viewHolder;

    private FavoriteMovieSQLDAO sqlDAO;
    private MoviesList moviesListinSQL;


    public static final int EDIT_ENTRY_REQUEST_CODE = 2;


    private Context context;
    private MoviesList itemsList;


    public FavoriteViewAdapter(MoviesList itemsList) {

        this.itemsList=itemsList;

    }
    public FavoriteViewAdapter(final Context context, MoviesList itemsList) {
        this.context=context;
        this.itemsList=itemsList;
        sqlDAO=new FavoriteMovieSQLDAO(context);

        new Thread( new Runnable() {
            @Override
            public void run() {
                sqlDAO=new FavoriteMovieSQLDAO(context);
                moviesListinSQL=sqlDAO.getAllSQL();;

            }
        } ).start();
    }
    public void set(MoviesList itemsList){
        this.itemsList=itemsList;
    }


    public MoviesList getItemList(){
        return this.itemsList;
    }

    private void changeBackGroundColorAndCheckData(FavoriteViewAdapter.ViewHolder vh, int positiom){

        String str=vh.tvName.getText().toString();
        Movie item=itemsList.findByTitle( str );


        if(item!=null){
            if(item.isbWatched()){
                //   vh.tvName.setBackgroundColor(Color.WHITE);
                vh.parent.setBackgroundColor(Color.WHITE);
                //   vh.tvName.setBackgroundColor( Color.YELLOW);//debug purpose
                //   vh.tvName.setTextColor( Color.BLACK ); //it repeats every 14 rows somehow

                //      vh.tvName.append( item.getMovieOverview().getTitle() );//debug
                item.setbWatched(   false );
                sqlDAO.update( item );

            }else{
                //   vh.tvName.setBackgroundColor(Color.RED);
                vh.parent.setBackgroundColor(Color.GRAY);
                //   vh.tvName.setTextColor( Color.WHITE );//it repeats every 14 rows somehow

                //   vh.tvName.setBackgroundColor( Color.BLUE); //debug purpose

                //       vh.tvName.append( item.getStrName() );//debug

                item.setbWatched(   true );
                sqlDAO.update( item );
            }
        }else {
        }


    }





        @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        sqlDAO=new FavoriteMovieSQLDAO( context ) ;

        View entryView = LayoutInflater.from(context).inflate( R.layout.image_list_view, viewGroup, false);


        return new FavoriteViewAdapter.ViewHolder(entryView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        if(i>56){
            System.out.printf( "test" );
        }
        final Movie it = this.itemsList.get(i);

        this.viewHolder=viewHolder;
        if(i>8){
            System.out.printf("8"); //debug
        }

        this.viewHolder=viewHolder;
        new Thread( new Runnable() {
            @Override
            public void run() {
            String path=it.getMovieOverview().getPoster_path();

            if(!path.equals( "" )&&!path.equals( "null" )&&path!=null){
                Bitmap bitmap= MovieApiDao.getPoster(  it.getMovieOverview().getPoster_path(),1 );
                if(!bitmap.equals( "" )) {
                    try {
                        viewHolder.ivImage.setImageBitmap(bitmap  );
                    }catch (Exception e){
                        System.out.printf(e.toString());

                    }
                }
            }
            }
        } ).start();

        viewHolder.tvName.setText(it.getMovieOverview().getTitle());

        if(it.getMovieOverview().getRelease_date().equals("0")){
            viewHolder.tvYear.setTextSize(14);
            viewHolder.tvYear.setText("(unknown)");

        }else{
            viewHolder.tvYear.setTextSize(20);
            viewHolder.tvYear.setText("("+it.getMovieOverview().getRelease_date().substring( 0,4 )+")");

        }
        if(it.isbWatched()){
            viewHolder.parent.setBackgroundColor( Color.GRAY );
        }else{
            viewHolder.parent.setBackgroundColor( Color.WHITE );
        }
        // viewHolder.ivImage.setImageDrawable( context.getResources() .getDrawable( it.getMovieOverview().getPoster_path() ));
        setEnterAnimation(viewHolder.parent, i);
    }

    @Override

    public int getItemCount() {

        return this.itemsList.size();

    }

    //3.   Add a click listener to each board in the list

    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private CardView parent;


        private ImageView ivImage;
        private TextView tvName,tvYear;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            this.parent = itemView.findViewById( R.id.element_parent);
            this.ivImage=itemView.findViewById( R.id.imageMovie );
            this.tvName= itemView.findViewById( R.id.text_name_to_choose);
            this.tvYear= itemView.findViewById( R.id.textYear);
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it

                changeBackGroundColorAndCheckData(this,position);
            }
        }

    }

    int lastPosition=0;
    private void setEnterAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}