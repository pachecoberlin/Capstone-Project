package de.pacheco.bakingapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import de.pacheco.bakingapp.activities.RecipeListFragment

/**
 * Implementation of App Widget functionality.
 */
class BakingTimeWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, this.javaClass))
        val text = intent.getStringExtra("howto")
        val widgetId = intent.getIntExtra("widgetId", -1)
        if (widgetId == -1) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, text)
            }
        } else {
            updateAppWidget(context, appWidgetManager, widgetId, text)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "")
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                            appWidgetId: Int, text: String?) {
            val widgetText: CharSequence = context.getString(R.string.appwidget_text)
            val newText = text ?: widgetText.toString()
            val views = RemoteViews(context.packageName, R.layout.baking_time_widget)
            views.setTextViewText(R.id.appwidget_text, newText)
            val configIntent = Intent(context, RecipeListFragment::class.java)
            configIntent.putExtra("widgetId", appWidgetId)
            val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
            views.setOnClickPendingIntent(R.id.appwidget_text, configPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}