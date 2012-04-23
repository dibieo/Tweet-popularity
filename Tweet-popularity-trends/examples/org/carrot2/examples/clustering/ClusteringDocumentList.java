
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.examples.ConsoleFormatter;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class ClusteringDocumentList
{
    public static void main(String [] args)
    {
        /* [[[start:clustering-document-list-intro]]]
         * 
         * <div>
         * <p>
         * The easiest way to get started with Carrot2 is to cluster a collection
         * of {@link org.carrot2.core.Document}s. Each document can consist of:
         * </p>
         * 
         * <ul>
         * <li>document content: a query-in-context snippet, document abstract or full text,</li>
         * <li>document title: optional, some clustering algorithms give more weight to document titles,</li>
         * <li>document URL: optional, used by the {@link org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, 
         * ignored by other algorithms.</li>
         * </ul>
         * 
         * <p>
         * To make the example short, the code shown below clusters only 5 documents. Use
         * at least 20 to get reasonable clusters. If you have access to the query that generated
         * the documents being clustered, you should also provide it to Carrot2 to get better clusters.
         * </p>
         * </div>
         * 
         * [[[end:clustering-document-list-intro]]]
         */
        {
            
            /**
             * Document has three parameters;
             */
       /* Prepare Carrot2 documents */
            final ArrayList<Document> documents = new ArrayList<Document>();
            
<<<<<<< HEAD
            final Document [] tweets = {
             new Document(" 1", "I love the NBA", "180 300"),
             new Document(" 2", "Jermey Ling is the best NBA plaer", "180 300"),
             new Document(" 3", "NBA finals are here", "180 300"),
             new Document(" 4", "Did you watch Real madrid  game", "180 300"),
             new Document(" 5", "Real madrid won hurray!", "180 300"),
             new Document(" 6", "Viva Madrid", "180 300"),
             new Document(" 7", "Tupac hologram was so cool", "180 300"),
             new Document(" 8", "I love the NBA", "180 300"),
             new Document("9", "Good game Real Madrid", "180 300"),
             new Document("10", "Congrats to all madrid fans", "180 300"),
             new Document("11", "Glad i woke up early for the madrid game", "180 300"),
             new Document("12", "Oh finals are coming soon", "180 300"),
             new Document("13", "hope i do well on final exams", "180 300"),
             new Document("14", "Finals!", "180 300"),
             new Document("15", "I'm tired of finals", "180 300"),
             new Document("16", "how do i learn how to program", "180 300"),
             new Document("17", "writing alot of java", "180 300"),
             new Document("18", "java is fun", "180 300"),
             new Document("19", "cochella was super fun especially with the tupac hologram", "180 300"),
             new Document("20", "tupac back to life", "180 300")

            };

=======
//            final Document [] tweets = {
//             new Document(" 1", "I love the NBA", "180 300"),
//             new Document(" 2", "Jermey Ling is the best NBA plaer", "180 300"),
//             new Document(" 3", "NBA finals are here", "180 300"),
//             new Document(" 4", "Did you watch Real madrid  game", "180 300"),
//             new Document(" 5", "Real madrid won hurray!", "180 300"),
//             new Document(" 6", "Viva Madrid", "180 300"),
//             new Document(" 7", "Tupac hologram was so cool", "180 300"),
//             new Document(" 8", "I love the NBA", "180 300"),
//             new Document("9", "Good game Real Madrid", "180 300"),
//             new Document("10", "Congrats to all madrid fans", "180 300"),
//             new Document("11", "Glad i woke up early for the madrid game", "180 300"),
//             new Document("12", "Oh finals are coming soon", "180 300"),
//             new Document("13", "hope i do well on final exams", "180 300"),
//             new Document("14", "Finals!", "180 300"),
//             new Document("15", "I'm tired of finals", "180 300"),
//             new Document("16", "how do i learn how to program", "180 300"),
//             new Document("17", "writing alot of java", "180 300"),
//             new Document("18", "java is fun", "180 300"),
//             new Document("19", "cochella was super fun especially with the tupac hologram", "180 300"),
//             new Document("20", "tupac back to life", "180 300")
//
//            };
            
            Document[] tweets=new CrawlingTweets().run(100);
>>>>>>> ff12ab3635c31165dc0aedea8b16aa74ec9a0cb8
            documents.addAll(Arrays.asList(tweets));

            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();

            /*
             * Perform clustering by topic using the Lingo algorithm. Lingo can 
             * take advantage of the original query, so we provide it along with the documents.
             */
            final ProcessingResult byTopicClusters = controller.process(documents, null,
                LingoClusteringAlgorithm.class);
            final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
            
            ConsoleFormatter.displayClusters(clustersByTopic);
       }
    }
}
