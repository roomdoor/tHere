import axios from 'axios';
import React, { useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import {
  useJourneyDetailActions,
  useJourneyDetailValue,
} from '../../contexts/journeyDetail';
import {
  useNewJourneyActions,
  useNewJourneyValue,
} from '../../contexts/newJourney';
import ContentsEditor from '../journeyUpload/ContentsEditor';
import JourneyUploadContainer from '../journeyUpload/JourneyUploadContainer';
import ThumbsUploader from '../journeyUpload/ThumbsUploader';

const JourneyFormBox = styled.form`
  position: relative;
  top: 60px;
  width: 100vw;
  height: 100vh;
  border: 1px solid black;
  display: flex;
`;

const SubmitBtnContainer = styled.div`
  position: absolute;
  top: 36px;
  right: 36px;

  & button {
    font-size: var(--font-small);
    background: var(--color-blue100);
    color: var(--color-gray100);
    border: none;

    &:first-child {
      margin-right: 14px;
      background: var(--color-gray500);
      color: var(--color-gray100);
    }
  }
`;

function JourneyUpdateContainer() {
  const journey = useJourneyDetailValue();
  const { updateData } = useJourneyDetailActions();
  const { initDatas, updateJourneyInfo } = useNewJourneyActions();
  const journeyInfo = useNewJourneyValue();

  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

    console.log('journeyInfo -> ');
    console.log(journeyInfo);
    // -- axios put 함수 호출
    // updateJourneyInfo(journeyInfo);

    Object.keys(journeyInfo).forEach((key) =>
      updateData(key, journeyInfo[key]),
    );

    alert('수정 완료');
    navigate(`/journey/${journey.journeyId}`);
  };

  const handleCancel = () => {
    navigate(-1);
  };

  useEffect(() => {
    initDatas(journey);
  }, []);

  return (
    <JourneyFormBox>
      <ThumbsUploader />
      <ContentsEditor />
      <SubmitBtnContainer>
        <button type="button" onClick={handleCancel}>
          취소
        </button>
        <button type="button" onClick={handleSubmit}>
          등록
        </button>
      </SubmitBtnContainer>
    </JourneyFormBox>
  );
}

export default JourneyUpdateContainer;