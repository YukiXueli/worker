/*
 * Copyright (C) 2015-2016 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SimpleListAdapterTest {
    @Test
    public void getItemCount_withoutItems() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_withItems() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");
        adapter.add("Item");
        adapter.add("Item");

        assertEquals(3, adapter.getItemCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_withoutItems() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.get(1);
    }

    @Test
    public void get_withItem() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");

        assertEquals("Item", adapter.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void set_withoutItems() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.set(1, "Item");
    }

    @Test
    public void set_withItem() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");

        assertEquals("Item", adapter.get(0));
        adapter.set(0, "Item 1");
        assertEquals("Item 1", adapter.get(0));
    }

    @Test
    public void add_item() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void add_items() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        List<String> items = new ArrayList<>();
        items.add("Item");
        items.add("Item");
        adapter.add(items);

        assertEquals(2, adapter.getItemCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void remove_withoutItems() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.remove(1);
    }

    @Test
    public void remove_withItem() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");

        assertEquals("Item", adapter.remove(0));
    }

    @Test
    public void clear() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        adapter.add("Item");
        adapter.add("Item");

        assertEquals(2, adapter.getItemCount());
        adapter.clear();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getOnItemClickListener_withoutListener() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        assertNull(adapter.getOnItemClickListener());
    }

    @Test
    public void getOnItemClickListener_withListener() {
        Context context = mock(Context.class);
        Adapter<String> adapter = new Adapter<>(context);

        SimpleListAdapter.OnItemClickListener listener = new SimpleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view) {
            }
        };
        adapter.setOnItemClickListener(listener);

        assertEquals(listener, adapter.getOnItemClickListener());
    }

    private class Adapter<T> extends SimpleListAdapter<T, ViewHolder> {
        public Adapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }
    }
}
