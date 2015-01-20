package com.itau.jingdong.task;

import java.util.concurrent.Callable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;


public class EMobileTask {


	public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback) {
		EMobileTask.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, null, false);
	}
	public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback) {
		EMobileTask.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, null, false,false);
	}
	public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final boolean pCancelable) {
		EMobileTask.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, null, pCancelable);
	}
	public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final boolean pCancelable) {
		EMobileTask.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, null, pCancelable,true);
	}
	public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		EMobileTask.doAsync(pContext, pTitleResID, pMessageResID, pCallable, pCallback, pExceptionCallback, false);
	}
	public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		EMobileTask.doAsync(pContext, pTitle, pMessage, pCallable, pCallback, pExceptionCallback, false,false);
	}
	public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback, final boolean pCancelable) {
		EMobileTask.doAsync(pContext, pContext.getString(pTitleResID), pContext.getString(pMessageResID), pCallable, pCallback, pExceptionCallback, pCancelable,true);
	}
	public static <T> void doAsync(final Context pContext, final CharSequence pTitle, final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback, final boolean pCancelable, final boolean Dialog) {
		new AsyncTask<Void, Void, T>() {
			private ProgressDialog mPD;
			private Exception mException = null;
			@Override
			public void onPreExecute() {
				if(Dialog){
					this.mPD = ProgressDialog.show(pContext, pTitle, pMessage, true, pCancelable);
				}
				if(pCancelable) {
					this.mPD.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface pDialogInterface) {
							pExceptionCallback.onCallback(new CancelledException());
							pDialogInterface.dismiss();
						}

					});
				}
				super.onPreExecute();
			}
			@Override
			public T doInBackground(final Void... params) {
				try {
					return pCallable.call();
				} catch (final Exception e) {
					this.mException = e;
				}
				return null;
			}
			@Override
			public void onPostExecute(final T result) {
				try {
					this.mPD.dismiss();
				} catch (final Exception e) {
					Log.e("Error", e.toString());
				}
				if(this.isCancelled()) {
					this.mException = new CancelledException();
				}
				if(this.mException == null) {
					pCallback.onCallback(result);
				} else {
					if(pExceptionCallback == null) {
						if (this.mException != null)
							Log.e("Error", this.mException.toString());
					} else {
						pExceptionCallback.onCallback(this.mException);
					}
				}
				super.onPostExecute(result);
			}
		}.execute((Void[]) null);
	}
	public static <T> void doProgressAsync(final Context pContext, final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback) {
		EMobileTask.doProgressAsync(pContext, pTitleResID, pCallable, pCallback, null);
	}
	public static <T> void doProgressAsync(final Context pContext, final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		new AsyncTask<Void, Integer, T>() {
			private ProgressDialog mPD;
			private Exception mException = null;
			@Override
			public void onPreExecute() {
				this.mPD = new ProgressDialog(pContext);
				this.mPD.setTitle(pTitleResID);
				this.mPD.setIcon(android.R.drawable.ic_menu_save);
				this.mPD.setIndeterminate(false);
				this.mPD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				this.mPD.show();
				super.onPreExecute();
			}
			@Override
			public T doInBackground(final Void... params) {
				try {
					return pCallable.call(new IProgressListener() {
						@Override
						public void onProgressChanged(final int pProgress) {
							onProgressUpdate(pProgress);
						}
					});
				} catch (final Exception e) {
					this.mException = e;
				}
				return null;
			}
			@Override
			public void onProgressUpdate(final Integer... values) {
				this.mPD.setProgress(values[0]);
			}
			@Override
			public void onPostExecute(final T result) {
				try {
					this.mPD.dismiss();
				} catch (final Exception e) {
					Log.e("Error", e.getLocalizedMessage());
					/* Nothing. */
				}
				if(this.isCancelled()) {
					this.mException = new CancelledException();
				}
				if(this.mException == null) {
					pCallback.onCallback(result);
				} else {
					if(pExceptionCallback == null) {
						Log.e("Error", this.mException.getLocalizedMessage());
					} else {
						pExceptionCallback.onCallback(this.mException);
					}
				}
				super.onPostExecute(result);
			}
		}.execute((Void[]) null);
	}
	public static <T> void doAsync(final Context pContext, final int pTitleResID, final int pMessageResID, final AsyncCallable<T> pAsyncCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		final ProgressDialog pd = ProgressDialog.show(pContext, pContext.getString(pTitleResID), pContext.getString(pMessageResID));
		pAsyncCallable.call(new Callback<T>() {
			@Override
			public void onCallback(final T result) {
				try {
					pd.dismiss();
				} catch (final Exception e) {
					Log.e("Error", e.getLocalizedMessage());
					/* Nothing. */
				}
				pCallback.onCallback(result);
			}
		}, pExceptionCallback);
	}
	
	public static class CancelledException extends Exception {
		private static final long serialVersionUID = -78123211381435595L;
	}

	
}
