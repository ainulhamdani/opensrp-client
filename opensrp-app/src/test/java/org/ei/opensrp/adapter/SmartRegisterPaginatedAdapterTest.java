package org.ei.opensrp.adapter;

import android.view.View;
import android.view.ViewGroup;

import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SmartRegisterPaginatedAdapterTest {

    @Test
    public void shouldReturn0PageCountFor0Clients() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(0);

        assertEquals(adapter.getCount(), 0);
        assertEquals(adapter.pageCount(), 0);
        assertEquals(adapter.currentPage(), 0);
        assertFalse(adapter.hasNextPage());
        assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void shouldReturn1PageCountFor20Clients() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(20);
        assertEquals(adapter.getCount(), 20);
        assertEquals(adapter.pageCount(), 1);
        assertEquals(adapter.currentPage(), 0);
        assertFalse(adapter.hasNextPage());
        assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void shouldReturn2PageCountFor21Clients() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(21);
        assertEquals(adapter.getCount(), 20);
        assertEquals(adapter.pageCount(), 2);
        assertEquals(adapter.currentPage(), 0);
        assertTrue(adapter.hasNextPage());
        assertFalse(adapter.hasPreviousPage());

        adapter.nextPage();

        assertEquals(adapter.getCount(), 1);
        assertEquals(adapter.currentPage(), 1);
        assertFalse(adapter.hasNextPage());
        assertTrue(adapter.hasPreviousPage());

        adapter.previousPage();

        assertEquals(adapter.currentPage(), 0);
        assertTrue(adapter.hasNextPage());
        assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void shouldReturn3PageCountFor50Clients() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        assertEquals(adapter.pageCount(), 3);
    }

    @Test
    public void getItemShouldReturnRespectiveItem() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        assertEquals(((ECClient) adapter.getItem(0)).name(), "Name0");
        assertEquals(((ECClient) adapter.getItem(49)).name(), "Name49");
    }

    @Test
    public void getViewShouldDelegateCallToProviderGetViewWithProperClient() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(50));
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        adapter.getView(0, null, null);
        assertEquals("Name0", fakeClientsProvider.getViewCurrentClient.name());

        adapter.getView(49, null, null);
        assertEquals("Name49", fakeClientsProvider.getViewCurrentClient.name());
    }

    @Test
    public void getItemIdShouldReturnTheActualPositionWithoutPagination() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(50));
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        assertEquals(0, adapter.getItemId(0));
        assertEquals(19, adapter.getItemId(19));
        adapter.nextPage();
        assertEquals(20, adapter.getItemId(0));
        assertEquals(39, adapter.getItemId(19));
        adapter.nextPage();
        assertEquals(40, adapter.getItemId(0));
        assertEquals(49, adapter.getItemId(9));
    }

    @Test
    public void updateClientsShouldApplyFilterToShowOnlyFiveClients() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        assertEquals(3, adapter.pageCount());
        assertEquals(20, adapter.getCount());

        adapter.refreshList(null, null, null, null);

        assertEquals(1, adapter.pageCount());
        assertEquals(5, adapter.getCount());
    }

    @Test
    public void paginationShouldWorkFor25ClientsPerPage() {
        SmartRegisterInMemoryPaginatedAdapter adapter = getAdapterWithFakeClients(50, 25);
        assertEquals(2, adapter.pageCount());
        assertEquals(25, adapter.getCount());
    }

    private SmartRegisterInMemoryPaginatedAdapter getAdapterWithFakeClients(int clientsCount) {
        return getAdapter(getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterInMemoryPaginatedAdapter getAdapterWithFakeClients(int clientsCount, int clientsPerPage) {
        return getAdapter(clientsPerPage, getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterClients getSmartRegisterClients(int count) {
        SmartRegisterClients clients = new SmartRegisterClients();
        for (int i = 0; i < count; i++) {
            clients.add(getClient(i));
        }
        return clients;
    }

    private SmartRegisterClient getClient(int i) {
        return new ECClient("abcd" + i, "name" + i, "husband" + i, "village" + i, 1000 + i);
    }

    private FakeClientsProvider getFakeProvider(SmartRegisterClients clients) {
        return new FakeClientsProvider(clients);
    }

    private SmartRegisterInMemoryPaginatedAdapter getAdapter(FakeClientsProvider provider) {
        return new SmartRegisterInMemoryPaginatedAdapter(20, provider);
    }

    private SmartRegisterInMemoryPaginatedAdapter getAdapter(int clientsPerPage, FakeClientsProvider provider) {
        return new SmartRegisterInMemoryPaginatedAdapter(clientsPerPage, provider);
    }

    private class FakeClientsProvider implements SmartRegisterClientsProvider {
        private SmartRegisterClients clients;

        public SmartRegisterClient getViewCurrentClient;

        public FakeClientsProvider(SmartRegisterClients clients) {
            this.clients = clients;
        }

        @Override
        public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
            this.getViewCurrentClient = client;
            return null;
        }

        @Override
        public SmartRegisterClients getClients() {
            return clients;
        }

        @Override
        public SmartRegisterClients updateClients(
                FilterOption villageFilter, ServiceModeOption serviceModeOption,
                SearchFilterOption searchFilter, SortOption sortOption) {
            return getSmartRegisterClients(5);
        }

        @Override
        public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

        }

        @Override
        public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
            return null;
        }

        @Override
        public View inflateLayoutForAdapter() {
            return null;
        }
    }
}
