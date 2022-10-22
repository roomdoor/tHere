import axios from 'axios';
import React, { createContext, useContext, useMemo, useState } from 'react';
import { addDatas } from '../lib/utills';

const NewJourneyValueContext = createContext();
const NewJourneyActionsContext = createContext();

const initialState = {
  memberEmail: '1',
  title: 'testTitle',
  startDate: '2022-10-16',
  endDate: '2022-10-17',
  numberOfPeople: 1,
  disclosure: 'public',
  thumbnail: [],
  introductionText: 'testIntroductionText',
  journeyThemes: ['힐링', '식도락'],
  region: '',
};

function NewJourneyProvider({ children }) {
  const [journeyInfo, setJourneyInfo] = useState(initialState);

  const actions = useMemo(
    () => ({
      updateData(name, value) {
        setJourneyInfo((prev) => ({ ...prev, [name]: value }));
      },

      addNewJourney(newJourney) {
        console.log(newJourney);
        addDatas(newJourney, 'http://localhost:8080/journey');
      },

      initState() {
        setJourneyInfo(initialState);
      },
    }),
    [],
  );

  return (
    <NewJourneyActionsContext.Provider value={actions}>
      <NewJourneyValueContext.Provider value={journeyInfo}>
        {children}
      </NewJourneyValueContext.Provider>
    </NewJourneyActionsContext.Provider>
  );
}

const useNewJourneyValue = () => {
  const value = useContext(NewJourneyValueContext);
  if (value === undefined) {
    throw new Error(
      'useNewJourneyValue should be used within JourneysProvider',
    );
  }
  return value;
};

const useNewJourneyActions = () => {
  const value = useContext(NewJourneyActionsContext);
  if (value === undefined) {
    throw new Error(
      'useNewJourneyActions should be used within JourneysProvider',
    );
  }
  return value;
};

export { useNewJourneyValue, useNewJourneyActions };
export default NewJourneyProvider;
