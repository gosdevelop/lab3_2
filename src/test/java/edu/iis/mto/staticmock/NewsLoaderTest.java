package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by Konrad Gos on 02.04.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class,NewsReaderFactory.class, PublishableNews.class})
public class NewsLoaderTest {
    private String readerType;
    private IncomingInfo subTypeNone = new IncomingInfo("subTypeNone", SubsciptionType.NONE);
    private IncomingInfo subTypeA = new IncomingInfo("subTypeA", SubsciptionType.A);
    private IncomingInfo subTypeB = new IncomingInfo("subTypeB", SubsciptionType.B);
    private IncomingInfo subTypeC = new IncomingInfo("subTypeC", SubsciptionType.C);
    @Before
    public void setUp() throws Exception {
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader configurationLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);

        readerType = "testType";
        Configuration configuration = new Configuration();
        Whitebox.setInternalState(configuration, "readerType", readerType);
        when(configurationLoader.loadConfiguration()).thenReturn(configuration);

        IncomingNews incomingNews = new IncomingNews();

        incomingNews.add(subTypeNone);
        incomingNews.add(subTypeA);
        incomingNews.add(subTypeB);
        incomingNews.add(subTypeC);

        NewsReader newsReader = mock(NewsReader.class);
        when(newsReader.read()).thenReturn(incomingNews);

        mockStatic(NewsReaderFactory.class);
        when(NewsReaderFactory.getReader(readerType)).thenReturn(newsReader);

        mockStatic(PublishableNews.class);
        when(PublishableNews.create()).thenReturn(new PublishableNewsFake());
    }

    @Test
    public void checkIfHasOnlyPublicNews() throws Exception {
        NewsLoader newsLoader = new NewsLoader();
        PublishableNewsFake publishableNewsFake = (PublishableNewsFake) newsLoader.loadNews();

        assertThat(publishableNewsFake.getPublicContent(), hasItem(subTypeNone.getContent()));
        assertThat(publishableNewsFake.getPublicContent(), not(hasItem(subTypeA.getContent())));
        assertThat(publishableNewsFake.getPublicContent(), not(hasItem(subTypeB.getContent())));
        assertThat(publishableNewsFake.getPublicContent(), not(hasItem(subTypeC.getContent())));
    }

    @Test
    public void checkIfHasOnlySubscribentNews() throws Exception {
        NewsLoader newsLoader = new NewsLoader();
        PublishableNewsFake publishableNewsFake = (PublishableNewsFake) newsLoader.loadNews();

        assertThat(publishableNewsFake.getSubscribentContent(), not(hasItem(subTypeNone.getContent())));
        assertThat(publishableNewsFake.getSubscribentContent(), hasItem(subTypeA.getContent()));
        assertThat(publishableNewsFake.getSubscribentContent(), hasItem(subTypeB.getContent()));
        assertThat(publishableNewsFake.getSubscribentContent(), hasItem(subTypeC.getContent()));
    }

    @Test
    public void checkIfNewsReaderCalledOnce() throws Exception {
        NewsLoader newsLoader = new NewsLoader();
        newsLoader.loadNews();
        verifyStatic(times(1));
        NewsReaderFactory.getReader(readerType);

    }

}