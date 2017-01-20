package com.pgmacdesign.googleapisamples.location;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pgmacdesign.googleapisamples.R;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmacdowell on 2017-01-20.
 */

public class AdapterAddressRecyclerview extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Address list

    private List<Address> mListAddress = new ArrayList<>();

    private LayoutInflater mInflater;
    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;
    
    private Context context;



    /**
     * Constructor
     * @param context Context (Needed for some calls)
     */
    public AdapterAddressRecyclerview(Context context){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * Update all Addresss and change the dataset
     * @param mListAddress
     */
    public void setAddresses(List<Address> mListAddress) {
        this.mListAddress = mListAddress;
        //update the adapter to reflect the new set of Data
        L.m("Data set has been updated");
        notifyDataSetChanged();
    }

    /**
     * For updating ONE Address
     * @param singleAddress
     * @param position
     */
    public void updateOneAddress(Address singleAddress, int position) {
        mListAddress.set(position, singleAddress);
        notifyItemChanged(position);

    }

    /**
     * For removing ONE Address
     * @param position
     */
    public void removeOneAddress(int position) {
        mListAddress.remove(position);
        notifyItemChanged(position);
    }

    /**
     * Constructs a Holder object.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        //Switch types here if needed
        view = mInflater.inflate(R.layout.simple_recyclerview_tv, parent, false);
        SimpleTVHolder viewHolder = new SimpleTVHolder(view);
        return viewHolder;

    }

    /**
     * Manages all the drawerData to be inserted into the recyclerview.
     * @param holder1 The view holder being used (Of type gift_individual layout)
     * @param position The position in the list being traversed (IE 0, 1, 2, 3)
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        //Set initial holders and objects
        Address currentAddress = mListAddress.get(position);

        SimpleTVHolder holder = (SimpleTVHolder) holder1;


        /*
        String str = new Gson().toJson(currentAddress, Address.class));
        str ==
        {
          "mAddressLines": {
            "1": "350 5th Avenue",
            "0": "Empire State Building",
            "3": "USA",
            "2": "New York, NY 10118"
          },
          "mAdminArea": "New York",
          "mCountryCode": "US",
          "mCountryName": "United States",
          "mFeatureName": "Empire State Building",
          "mHasLatitude": true,
          "mHasLongitude": true,
          "mLatitude": 40.7484405,
          "mLocale": "en_US",
          "mLocality": "New York",
          "mLongitude": -73.9856644,
          "mMaxAddressLineIndex": 3,
          "mPostalCode": "10118",
          "mSubAdminArea": "New York County",
          "mSubLocality": "Manhattan",
          "mSubThoroughfare": "350",
          "mThoroughfare": "5th Avenue"
        }
         */
        List<String> strList = new ArrayList<>();
        for(int i = 0; i < currentAddress.getMaxAddressLineIndex(); i++){
            strList.add(currentAddress.getAddressLine(i));
        }
        String str = TextUtils.join(System.getProperty("line.separator"), strList);

        holder.simple_recyclerview_tv_tv.setText(str);

        //Animation here
        if (position > mPreviousPosition) {
            //AnimationUtilities.animateMyView(holder1, 500, Constants.IN_FLIP_X);
        } else {
            //AnimationUtilities.animateMyView(holder1, 400, Constants.IN_RIGHT_SLIDE);
        }
        mPreviousPosition = position;


        return;
    }


    /**
     * @return Returns the size of the list of Contest Items
     */
    @Override
    public int getItemCount() {
        try {
            return mListAddress.size();
        } catch (Exception e){
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    /**
     * For simple TV 
     */
    class SimpleTVHolder extends RecyclerView.ViewHolder {
        private TextView simple_recyclerview_tv_tv;

        //Address Holder class constructor
        public SimpleTVHolder(View itemView) {
            super(itemView);
            simple_recyclerview_tv_tv = (TextView) itemView.findViewById(
                    R.id.simple_recyclerview_tv_tv);
        }
    }
}

