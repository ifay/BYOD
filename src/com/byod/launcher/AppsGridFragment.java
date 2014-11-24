package com.byod.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.byod.PhoneTabHostAcitivity;
import com.byod.R;

import java.util.ArrayList;

public class AppsGridFragment extends GridFragment implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {
    private static final String TAG = "AppsGridFragment";
    AppListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No Applications");

        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);

        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        return new AppsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        mAdapter.setData(apps);

        if (isResumed()) {
            setGridShown(true);
        } else {
            setGridShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        AppModel app = (AppModel) getGridAdapter().getItem(position);
        if (app != null) {
            Intent intent = null;
            Context context = getActivity().getApplicationContext();
            if (null == app.getAppInfo()) {// 应用内应用
                Resources resources = context.getResources();
                if (resources.getString(R.string.contactsActivityLabel).equals(app.getLabel())) {
                    intent = new Intent(context, PhoneTabHostAcitivity.class);
                    intent.putExtra(PhoneTabHostAcitivity.EXTRA_PAGE, 0);
                } else if (resources.getString(R.string.dialActivityLabel).equals(app.getLabel())) {
                    intent = new Intent(context, PhoneTabHostAcitivity.class);
                    intent.putExtra(PhoneTabHostAcitivity.EXTRA_PAGE, 1);
                } else if (resources.getString(R.string.smsActivityLabel).equals(app.getLabel())) {
                    intent = new Intent(context, PhoneTabHostAcitivity.class);
                    intent.putExtra(PhoneTabHostAcitivity.EXTRA_PAGE, 2);
                }
            } else {
                intent = context.getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());
            }
            Log.d(TAG, "startActivity: " + intent.toString());
            if (intent != null) {
                startActivity(intent);
            }
        }
    }
}