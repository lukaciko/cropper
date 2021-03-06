/*
 * Copyright 2015 Luka Cindro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cropper.lib.Cropper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
	private ImageView croppedImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button) findViewById(R.id.pick_image);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Cropper.pick(MainActivity.this);
			}
		});

		croppedImageView = (ImageView) findViewById(R.id.cropped_image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Cropper.PICK && data != null)
		{
			Uri uri = data.getData();
			Log.i(MainActivity.class.getSimpleName(), "Image uri " + uri.toString());

			File file = new File(getExternalFilesDir(null), "cropped_image.png");

			View decorView = getWindow().getDecorView();
			Cropper.crop(getApplicationContext(), uri, Uri.fromFile(file))
				.outputSize(decorView.getWidth(), decorView.getHeight() / 2)
				.scale(true)
				.start(this);
		}
		else if (requestCode == Cropper.CROP && data != null)
		{
			String filePath = data.getStringExtra(Cropper.SAVE_PATH);
			BitmapTask task = new BitmapTask();
			task.execute(filePath);
		}
	}

	private class BitmapTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... strings)
		{
			String path = strings[0];
			InputStream is = null;
			Bitmap bitmap = null;
			try
			{
				is = new URL(path).openStream();
				bitmap = BitmapFactory.decodeStream(is);
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException e)
					{
					}
				}
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			super.onPostExecute(bitmap);
			croppedImageView.setImageBitmap(bitmap);
		}
	}
}
