/*
 * Copyright @ 2015 Atlassian Pty Ltd
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
package org.jitsi_modified.sctp4j;

import java.io.IOException;

/**
 * Sample that uses two <tt>SctpSocket</tt>s with {@link DirectLink}.
 *
 * @author Pawel Domas
 */
public class SampleLoop
{
    /**
     * The logger.
     */
//    private final static Logger logger = Logger.getLogger(org.jitsi.sctp4j.SampleLoop.class);

    public static void main(String[] args) throws Exception
    {
        Sctp.init();

        final SctpSocket server = Sctp.createSocket(5001);
        final SctpSocket client = Sctp.createSocket(5002);
        
        DirectLink link = new DirectLink(server, client);
        server.setLink(link);
        client.setLink(link);
        
        // Make server passive
        server.listen();

        // Client thread
        new Thread(
          new Runnable()
          {
            public void run()
            {
                try
                {
                    client.connect(server.getPort());
//                    logger.info("Client: connect");

                    try { Thread.sleep(1000); } catch(Exception e) { }

                    int sent = client.send(new byte[200], false, 0, 0);
//                    logger.info("Client sent: " + sent);

                }
                catch (IOException e)
                {
//                    logger.error(e, e);
                }
            }
          }
        ).start();

        server.setDataCallback(
            new SctpDataCallback()
            {
                @Override
                public void onSctpPacket(byte[] data, int sid, int ssn, int tsn,
                                         long ppid,
                                         int context, int flags)
                {
//                    logger.info("Server got some data: " + data.length
//                                + " stream: " + sid
//                                + " payload protocol id: " + ppid);
                }
            }
        );

        Thread.sleep(5*1000);
        
        server.close();
        client.close();
        
        Sctp.finish();
    }
}
