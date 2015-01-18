/*
 * Copyright (C) 2013-2015 Mike Cunningham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emetophobe.permissionlist.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.emetophobe.permissionlist.adapters.PermissionListAdapter;
import com.emetophobe.permissionlist.SettingsHelper;
import com.emetophobe.permissionlist.activities.PermissionDetailActivity;
import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;


public class PermissionListFragment extends AbstractListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up the adapter.
		mAdapter = new PermissionListAdapter(getActivity());
		setListAdapter(mAdapter);

		// Load the permission list.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		setLoading(true);

		// Set up the selection clause.
		String selection = Permissions.PERMISSION_NAME + " IS NOT NULL";
		if (!SettingsHelper.getShowSystemApps(getActivity())) {
			selection += " AND " + Permissions.IS_SYSTEM + " = 0";
		}

		// Set the permission sort order preference.
		String sortOrder = SettingsHelper.getPermissionSortOrder(getActivity())
				? Permissions.PERMISSION_NAME + " ASC" : "count DESC";

		// Create the loader.
		return new CursorLoader(getActivity(), Permissions.PERMISSIONS_URI, new String[]{Permissions._ID,
				Permissions.PERMISSION_NAME, Permissions.APP_NAME, "Count(app_name) AS count"}, selection, null,
				sortOrder);
	}

	/** Start the permission detail activity when a list item is clicked. */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) mAdapter.getItem(position);
		if (cursor != null) {
			String permissionName = cursor.getString(cursor.getColumnIndex(Permissions.PERMISSION_NAME));
			Intent intent = new Intent(getActivity(), PermissionDetailActivity.class);
			intent.putExtra(PermissionDetailActivity.EXTRA_PERMISSION_NAME, permissionName);
			startActivity(intent);
		}
	}
}
