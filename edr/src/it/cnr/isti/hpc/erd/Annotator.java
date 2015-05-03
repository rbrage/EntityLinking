/**
 *  Copyright 2014 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.erd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import EntityLinkingELNAB.Annotation;
import EntityLinkingELNAB.ELNAB;


/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 15, 2014
 */

public class Annotator {

	public Annotator() {

	}

	public List<Annotation> annotate(String runId, String textID, String text) throws IOException {
		List<Annotation> annotations = new ArrayList<Annotation>();
		
		Annotation a = new Annotation();
		a.setDocId(textID);
		a.setBeginOffset(18);
		a.setEndOffset(23);
		a.setPrimaryID("/m/03_3d");
		a.setSecondID("https://www.freebase.com//m/03_3d");
		a.setMentionText("JAPAN");
		a.setScore1(3);
		
		Annotation b = new Annotation();
		b.setDocId(textID);
		b.setBeginOffset(183);
		b.setEndOffset(188);
		b.setPrimaryID("/m/03_3d");
		b.setSecondID("https://www.freebase.com//m/03_3d");
		b.setMentionText("JAPAN");
		b.setScore1(3);
		
		annotations.add(a);
		annotations.add(b);
		
		return annotations;
	}

}
