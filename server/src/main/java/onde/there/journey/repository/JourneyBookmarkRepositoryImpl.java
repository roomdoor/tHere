package onde.there.journey.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static onde.there.domain.QJourneyBookmark.journeyBookmark;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import onde.there.domain.JourneyBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class JourneyBookmarkRepositoryImpl implements JourneyBookmarkRepositoryCustom {

	private final JPQLQueryFactory jpaQueryFactory;

//	@Override
//	public Page<JourneyBookmarkPageResponse> getBookmarkPage(String memberId, Pageable pageable) {
//		List<JourneyBookmarkPageResponse> fetch = jpaQueryFactory
//			.select(Projections.constructor(JourneyBookmarkPageResponse.class,
//				journeyBookmark.journey.id.as("journeyId"),
//				journeyBookmark.id.as("journeyBookmarkId"),
//				journeyBookmark.member.id.as("memberId"),
//				journeyBookmark.journey.title.as("title"),
//				journeyBookmark.journey.startDate.as("startDate"),
//				journeyBookmark.journey.endDate.as("endDate"),
//				journeyBookmark.journey.numberOfPeople.as("numberOfPeople"),
//				journeyBookmark.journey.disclosure.as("disclosure"),
//				journeyBookmark.journey.journeyThemes.as("journeyThemes"),
//				journeyBookmark.journey.introductionText.as("introductionText"),
//				journeyBookmark.journey.region.as("region"),
//				journeyBookmark.journey.journeyThumbnailUrl.as("journeyThumbnailUrl")))
//			.from(journeyBookmark)
//
//			.where(memberEq(memberId))
//			.offset(pageable.getOffset())
//			.limit(pageable.getPageSize())
//            .fetch();
//
//		JPQLQuery<JourneyBookmark> count = jpaQueryFactory
//			.selectFrom(journeyBookmark)
//			.join(journeyBookmark.journey, journey)
//			.join(journeyBookmark.member, member)
//			.where(memberEq(memberId));
//
//		return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
//	}

	@Override
	public Page<JourneyBookmark> getBookmarkPage(String memberId, Pageable pageable) {
		List<JourneyBookmark> fetch = jpaQueryFactory
			.selectFrom(journeyBookmark)
            .where(memberEq(memberId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
		JPQLQuery<JourneyBookmark> count = jpaQueryFactory
			.selectFrom(journeyBookmark)
			.where(memberEq(memberId));

		return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
	}

	@Override
	public Tuple bookmarkConfirmation(Long journeyId, String memberId) {

		return jpaQueryFactory
			.select(journeyBookmark.journey.id.countDistinct(), select()
				.from(journeyBookmark)
				.where(journeyBookmark.journey.id.eq(journeyId),
					journeyBookmark.member.id.eq(memberId)).exists())
			.where(journeyBookmark.journey.id.eq(journeyId))
			.from(journeyBookmark)
			.fetchOne();
	}

	private BooleanExpression memberEq(String memberId) {
		return memberId != null ? journeyBookmark.member.id.eq(memberId) : null;
	}


}
