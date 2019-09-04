/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.course.learningpath.manager;

import org.olat.course.learningpath.LearningPathConfigs;
import org.olat.course.learningpath.LearningPathService;
import org.olat.course.learningpath.evaluation.AccessEvaluator;
import org.olat.course.learningpath.evaluation.DurationEvaluatorProvider;
import org.olat.course.learningpath.evaluation.LinearAccessEvaluator;
import org.olat.course.learningpath.evaluation.ObligationEvaluatorProvider;
import org.olat.course.learningpath.evaluation.StatusEvaluatorProvider;
import org.olat.course.nodes.CourseNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 1 Sep 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
public class LearningPathServiceImpl implements LearningPathService {
	
	private final static AccessEvaluator ACCESS_EVALUATOR = new LinearAccessEvaluator();
	
	@Autowired
	private LearningPathRegistry registry;

	@Override
	public LearningPathConfigs getConfigs(CourseNode courseNode) {
		return registry.getLearningPathNodeHandler(courseNode).getConfigs(courseNode);
	}

	@Override
	public ObligationEvaluatorProvider getObligationEvaluatorProvider() {
		return registry.getObligationEvaluatorProvider();
	}

	@Override
	public StatusEvaluatorProvider getStatusEvaluatorProvider() {
		return registry.getLinearStatusEvaluatorProvider();
	}

	@Override
	public DurationEvaluatorProvider getDurationEvaluatorProvider() {
		return registry.getDurationEvaluatorProvider();
	}

	@Override
	public AccessEvaluator getAccessEvaluator() {
		return ACCESS_EVALUATOR;
	}

}