import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';

const BracketContainer = styled.div`
  overflow-x: auto;
  padding: ${({ theme }) => theme.spacing[6]};
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
`;

const BracketTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const BracketGrid = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[8]};
  min-width: max-content;
`;

const Round = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  min-width: 200px;
`;

const RoundTitle = styled.div`
  text-align: center;
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[700]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const MatchContainer = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const Match = styled.div`
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  background: white;
  overflow: hidden;
`;

const Participant = styled.div<{ isWinner?: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]};
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: ${({ isWinner, theme }) => 
    isWinner ? theme.colors.success[50] : 'white'};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  font-weight: ${({ isWinner, theme }) => 
    isWinner ? theme.fontWeights.semibold : theme.fontWeights.normal};

  &:last-child {
    border-bottom: none;
  }
`;

const ParticipantName = styled.span`
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const Score = styled.span`
  color: ${({ theme }) => theme.colors.gray[700]};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const EmptySlot = styled.div`
  padding: ${({ theme }) => theme.spacing[3]};
  color: ${({ theme }) => theme.colors.gray[400]};
  font-style: italic;
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const NoDataMessage = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[8]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

interface BracketMatch {
  id: string;
  participant1?: {
    name: string;
    score?: number;
    isWinner?: boolean;
  };
  participant2?: {
    name: string;
    score?: number;
    isWinner?: boolean;
  };
}

interface BracketRound {
  name: string;
  matches: BracketMatch[];
}

interface TournamentBracketProps {
  bracket?: BracketRound[];
  categoryName?: string;
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ bracket, categoryName }) => {
  const { t } = useTranslation();

  if (!bracket || bracket.length === 0) {
    return (
      <BracketContainer>
        <NoDataMessage>
          {t('tournaments.bracket.noData')}
        </NoDataMessage>
      </BracketContainer>
    );
  }

  return (
    <BracketContainer>
      {categoryName && <BracketTitle>{categoryName}</BracketTitle>}
      
      <BracketGrid>
        {bracket.map((round, roundIndex) => (
          <Round key={roundIndex}>
            <RoundTitle>{round.name}</RoundTitle>
            {round.matches.map((match) => (
              <MatchContainer key={match.id}>
                <Match>
                  {match.participant1 ? (
                    <Participant isWinner={match.participant1.isWinner}>
                      <ParticipantName>{match.participant1.name}</ParticipantName>
                      {match.participant1.score !== undefined && (
                        <Score>{match.participant1.score}</Score>
                      )}
                    </Participant>
                  ) : (
                    <EmptySlot>{t('tournaments.bracket.tbd')}</EmptySlot>
                  )}
                  
                  {match.participant2 ? (
                    <Participant isWinner={match.participant2.isWinner}>
                      <ParticipantName>{match.participant2.name}</ParticipantName>
                      {match.participant2.score !== undefined && (
                        <Score>{match.participant2.score}</Score>
                      )}
                    </Participant>
                  ) : (
                    <EmptySlot>{t('tournaments.bracket.tbd')}</EmptySlot>
                  )}
                </Match>
              </MatchContainer>
            ))}
          </Round>
        ))}
      </BracketGrid>
    </BracketContainer>
  );
};

export default TournamentBracket;
