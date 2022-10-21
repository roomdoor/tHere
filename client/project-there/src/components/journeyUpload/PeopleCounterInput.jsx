import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import {
  useNewJourneyActions,
  useNewJourneyValue,
} from '../../contexts/newJourney';

const PeopleCounterBox = styled.div`
  width: 100%;
  height: 8%;
  background: white;
  border: 1px solid black;
  margin-top: 16px;
  display: flex;
  justify-content: left;
  align-items: center;
`;

const CounterTitle = styled.span`
  margin-right: 8px;
`;

const Button = styled.button`
  background: white;
  border: 1px solid black;
  border-radius: 50%;
  padding: 4px;
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const ValueBox = styled.span`
  margin: 0 12px;
`;

function PeopleCounterInput() {
  const { numberOfPeople } = useNewJourneyValue();
  const { updateData } = useNewJourneyActions();

  const handleUpdateNumOfPeople = ({ target }) => {
    if (target.textContent === '+') {
      updateData('numberOfPeople', numberOfPeople + 1);
    } else if (target.textContent === '-') {
      if (numberOfPeople === 0) return;
      updateData('numberOfPeople', numberOfPeople - 1);
    }
  };

  return (
    <PeopleCounterBox onClick={handleUpdateNumOfPeople}>
      <CounterTitle>여행자</CounterTitle>
      <Button type="button">-</Button>
      <ValueBox>{numberOfPeople}</ValueBox>
      <Button type="button">+</Button>
      <button type="button">확인</button>
    </PeopleCounterBox>
  );
}

export default PeopleCounterInput;
