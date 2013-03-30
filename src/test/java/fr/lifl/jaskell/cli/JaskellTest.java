/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class JaskellTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Test
    public void compilesAndRunOverGivenInputStreamThenSendResultToOutputStream() throws Exception {
        File sourceFile = new File(getClass().getResource("/simple.k").getFile());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Jaskell jaskell = new Jaskell(new FileInputStream(sourceFile),bytes);

        jaskell.run();

        Exception exception = jaskell.getException();
        if(exception != null) {
            exception.printStackTrace();
        }
        assertThat(exception).isNull();
        assertThat(new String(bytes.toByteArray())).isEqualTo("24");
    }
}
