package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by Konrad Gos on 02.04.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class,NewsReaderFactory.class, PublishableNews.class})
public class NewsLoaderTest {
    private String readerType;

    @Before
    public void setUp() throws Exception {
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader configurationLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);

        readerType = "testType";
        Configuration configuration = new Configuration();
        Whitebox.setInternalState(configuration, "readerType", readerType);
        when(configurationLoader.loadConfiguration()).thenReturn(configuration);

        mockStatic(NewsReaderFactory.class);
        IncomingNews incomingNews = new IncomingNews();

        incomingNews.add(new IncomingInfo("subTypeNone", SubsciptionType.NONE));
        incomingNews.add(new IncomingInfo("subTypeA", SubsciptionType.A));
        incomingNews.add(new IncomingInfo("subTypeB", SubsciptionType.B));
        incomingNews.add(new IncomingInfo("subTypeC", SubsciptionType.C));

        NewsReader newsReader = mock(NewsReader.class);
        when(newsReader.read()).thenReturn(incomingNews);
        when(NewsReaderFactory.getReader(readerType)).thenReturn(newsReader);

        mockStatic(PublishableNews.class);
        when(PublishableNews.create()).thenReturn(new PublishableNewsFake());
    }
    @Test
    public void checkIfHasOnlyPublicNews() throws Exception {
        NewsLoader newsLoader = new NewsLoader();
        PublishableNewsFake publishableNewsFake = (PublishableNewsFake) newsLoader.loadNews();

        assertThat(publishableNewsFake.getPublicContent().get(0), is(equalTo(("subTypeNone"))));
    }

}