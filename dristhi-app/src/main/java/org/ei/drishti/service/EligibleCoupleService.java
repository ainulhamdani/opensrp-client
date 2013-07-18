package org.ei.drishti.service;

import org.ei.drishti.AllConstants;
import org.ei.drishti.domain.TimelineEvent;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllBeneficiaries;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllTimelineEvents;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.drishti.AllConstants.*;
import static org.ei.drishti.domain.TimelineEvent.forChangeOfFPMethod;
import static org.ei.drishti.util.EasyMap.mapOf;

public class EligibleCoupleService {
    private final AllEligibleCouples allEligibleCouples;
    private final AllTimelineEvents allTimelineEvents;
    private final AllBeneficiaries allBeneficiaries;

    public EligibleCoupleService(AllEligibleCouples allEligibleCouples, AllTimelineEvents allTimelineEvents, AllBeneficiaries allBeneficiaries) {
        this.allEligibleCouples = allEligibleCouples;
        this.allTimelineEvents = allTimelineEvents;
        this.allBeneficiaries = allBeneficiaries;
    }

    public void register(FormSubmission submission) {
        if (isNotBlank(submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE))) {
            allTimelineEvents.add(TimelineEvent.forECRegistered(submission.entityId(), submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE)));
        }
    }

    public void fpComplications(FormSubmission submission) {
    }

    public void fpChange(FormSubmission submission) {
        allTimelineEvents.add(forChangeOfFPMethod(submission.entityId(), submission.getFieldValue(CURRENT_FP_METHOD_FIELD_NAME),
                submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME), submission.getFieldValue(FAMILY_PLANNING_METHOD_CHANGE_DATE_FIELD_NAME)));
        allEligibleCouples.mergeDetails(submission.entityId(), mapOf(CURRENT_FP_METHOD_FIELD_NAME, submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME)));
    }

    public void renewFPProduct(FormSubmission submission) {
    }

    public void closeEligibleCouple(FormSubmission submission) {
        allEligibleCouples.close(submission.entityId());
        allBeneficiaries.closeAllMothersForEC(submission.entityId());
    }
}
