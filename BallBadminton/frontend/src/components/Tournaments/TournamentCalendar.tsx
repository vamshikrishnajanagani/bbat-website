import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { Tournament } from '../../store/slices/tournamentsSlice';

const CalendarContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const CalendarHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const MonthTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const NavButton = styled.button`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.primary[600]};
  color: white;
  border: none;
  border-radius: ${({ theme }) => theme.borderRadius.md};
  cursor: pointer;
  font-weight: ${({ theme }) => theme.fontWeights.medium};

  &:hover {
    background: ${({ theme }) => theme.colors.primary[700]};
  }
`;

const CalendarGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: ${({ theme }) => theme.spacing[2]};
`;

const DayHeader = styled.div`
  text-align: center;
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[700]};
  padding: ${({ theme }) => theme.spacing[2]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const DayCell = styled.div<{ isCurrentMonth: boolean; hasEvents: boolean }>`
  min-height: 100px;
  padding: ${({ theme }) => theme.spacing[2]};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  background: ${({ isCurrentMonth, theme }) => 
    isCurrentMonth ? 'white' : theme.colors.gray[50]};
  opacity: ${({ isCurrentMonth }) => isCurrentMonth ? 1 : 0.5};
  cursor: ${({ hasEvents }) => hasEvents ? 'pointer' : 'default'};

  &:hover {
    background: ${({ hasEvents, theme }) => 
      hasEvents ? theme.colors.gray[50] : 'white'};
  }
`;

const DayNumber = styled.div`
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[1]};
`;

const EventDot = styled.div<{ status: string }>`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: ${({ status, theme }) => {
    switch (status) {
      case 'Upcoming': return theme.colors.info[500];
      case 'Ongoing': return theme.colors.success[500];
      case 'Completed': return theme.colors.gray[500];
      default: return theme.colors.gray[500];
    }
  }};
  display: inline-block;
  margin-right: ${({ theme }) => theme.spacing[1]};
`;

const EventItem = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[700]};
  padding: ${({ theme }) => theme.spacing[1]};
  margin-bottom: ${({ theme }) => theme.spacing[1]};
  background: ${({ theme }) => theme.colors.gray[100]};
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  display: flex;
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

interface TournamentCalendarProps {
  tournaments: Tournament[];
  onTournamentClick: (id: string) => void;
}

const TournamentCalendar: React.FC<TournamentCalendarProps> = ({ tournaments, onTournamentClick }) => {
  const { t } = useTranslation();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [calendarDays, setCalendarDays] = useState<Date[]>([]);

  useEffect(() => {
    generateCalendar();
  }, [currentDate]);

  const generateCalendar = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - startDate.getDay());
    
    const days: Date[] = [];
    const currentDay = new Date(startDate);
    
    for (let i = 0; i < 42; i++) {
      days.push(new Date(currentDay));
      currentDay.setDate(currentDay.getDate() + 1);
    }
    
    setCalendarDays(days);
  };

  const getTournamentsForDay = (date: Date) => {
    return tournaments.filter(tournament => {
      const startDate = new Date(tournament.startDate);
      const endDate = new Date(tournament.endDate);
      return date >= startDate && date <= endDate;
    });
  };

  const navigateMonth = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + (direction === 'next' ? 1 : -1));
    setCurrentDate(newDate);
  };

  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === currentDate.getMonth();
  };

  const monthName = currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  return (
    <CalendarContainer>
      <CalendarHeader>
        <NavButton onClick={() => navigateMonth('prev')}>← Previous</NavButton>
        <MonthTitle>{monthName}</MonthTitle>
        <NavButton onClick={() => navigateMonth('next')}>Next →</NavButton>
      </CalendarHeader>

      <CalendarGrid>
        {dayNames.map(day => (
          <DayHeader key={day}>{day}</DayHeader>
        ))}
        
        {calendarDays.map((day, index) => {
          const dayTournaments = getTournamentsForDay(day);
          return (
            <DayCell
              key={index}
              isCurrentMonth={isCurrentMonth(day)}
              hasEvents={dayTournaments.length > 0}
            >
              <DayNumber>{day.getDate()}</DayNumber>
              {dayTournaments.slice(0, 2).map(tournament => (
                <EventItem
                  key={tournament.id}
                  onClick={() => onTournamentClick(tournament.id)}
                >
                  <EventDot status={tournament.status} />
                  <span>{tournament.name}</span>
                </EventItem>
              ))}
              {dayTournaments.length > 2 && (
                <EventItem>+{dayTournaments.length - 2} more</EventItem>
              )}
            </DayCell>
          );
        })}
      </CalendarGrid>
    </CalendarContainer>
  );
};

export default TournamentCalendar;
