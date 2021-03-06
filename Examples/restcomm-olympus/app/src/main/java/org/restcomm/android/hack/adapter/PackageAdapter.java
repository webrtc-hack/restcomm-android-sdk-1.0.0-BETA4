package org.restcomm.android.hack.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.restcomm.android.hack.activity.RegisterDeviceActivity;
import org.restcomm.android.hack.api.CallbackInterface;
import org.restcomm.android.hack.api.UserInterface;
import org.restcomm.android.hack.db.Auths;
import org.restcomm.android.hack.model.MyPackage;
import org.restcomm.android.hack.model.RestError;
import org.restcomm.android.hack.model.User;
import org.restcomm.android.hack.model.body.BPurchase;
import org.restcomm.android.hack.utils.Static;
import org.restcomm.android.olympus.R;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by samialmouhtaseb on 15/10/16.
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Static.URL).build();

    private Context context;
    private ArrayList<MyPackage> packages;

    public PackageAdapter(Context context, ArrayList<MyPackage> packages) {
        this.context = context;
        this.packages = packages;
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_packages, parent, false);
        return new PackageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageViewHolder holder, int position) {
        final MyPackage myPackage = this.packages.get(position);
        holder.tv_name.setText("Name: " + myPackage.getName());
        holder.tv_description.setText("Description: " + myPackage.getDescription());
        holder.tv_devices.setText("Num of Devices: " + myPackage.getDevices());
        holder.tv_hours.setText("Subscription Limit: " + myPackage.getHours() + " Hours");
        holder.tv_price.setText("Subscription Price: " + myPackage.getPrice() + " MYR");

        holder.llCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInterface userInterface = adapter.create(UserInterface.class);
                Auths dbAuth = Auths.getAuth();

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                BPurchase bPurchase = new BPurchase(myPackage.getId());
                userInterface.purchase(dbAuth.accessToken, bPurchase, new CallbackInterface<User>(context) {
                    @Override
                    public void failure(RestError restError) {
                        progressDialog.dismiss();
                        Log.e("error", restError.getCode() + "");
                        Toast.makeText(context, restError.getErrorDetails(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void success(User user, Response response) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(context, RegisterDeviceActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout llCard;
        protected TextView tv_name;
        protected TextView tv_devices;
        protected TextView tv_hours;
        protected TextView tv_price;
        protected TextView tv_description;

        public PackageViewHolder(View itemView) {
            super(itemView);

            llCard = (LinearLayout) itemView.findViewById(R.id.llCard);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_devices = (TextView) itemView.findViewById(R.id.tv_devices);
            tv_hours = (TextView) itemView.findViewById(R.id.tv_hours);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
        }
    }
}
