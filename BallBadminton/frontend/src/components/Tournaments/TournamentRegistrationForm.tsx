import React, { useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { Tournament } from '../../store/slices/tournamentsSlice';
import { tournamentsService, TournamentRegistration } from '../../services/tournamentsService';
import Button from '../UI/Button';
import Modal from '../UI/Modal';

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[2]};
`;

const Label = styled.label`
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const Input = styled.input`
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const Select = styled.select`
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const ErrorMessage = styled.div`
  color: ${({ theme }) => theme.colors.error[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  margin-top: ${({ theme }) => theme.spacing[1]};
`;

const SuccessMessage = styled.div`
  color: ${({ theme }) => theme.colors.success[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  padding: ${({ theme }) => theme.spacing[3]};
  background: ${({ theme }) => theme.colors.success[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  justify-content: flex-end;
  margin-top: ${({ theme }) => theme.spacing[4]};
`;

const TournamentInfo = styled.div`
  background: ${({ theme }) => theme.colors.gray[50]};
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const InfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

interface TournamentRegistrationFormProps {
  tournament: Tournament;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const TournamentRegistrationForm: React.FC<TournamentRegistrationFormProps> = ({
  tournament,
  isOpen,
  onClose,
  onSuccess
}) => {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<Partial<TournamentRegistration>>({
    tournamentId: tournament.id,
    categoryId: '',
    playerName: '',
    playerEmail: '',
    playerPhone: '',
    district: '',
    emergencyContact: ''
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitSuccess, setSubmitSuccess] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.categoryId) {
      newErrors.categoryId = t('tournaments.registration.errors.categoryRequired');
    }
    if (!formData.playerName?.trim()) {
      newErrors.playerName = t('tournaments.registration.errors.nameRequired');
    }
    if (!formData.playerEmail?.trim()) {
      newErrors.playerEmail = t('tournaments.registration.errors.emailRequired');
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.playerEmail)) {
      newErrors.playerEmail = t('tournaments.registration.errors.emailInvalid');
    }
    if (!formData.playerPhone?.trim()) {
      newErrors.playerPhone = t('tournaments.registration.errors.phoneRequired');
    }
    if (!formData.district?.trim()) {
      newErrors.district = t('tournaments.registration.errors.districtRequired');
    }
    if (!formData.emergencyContact?.trim()) {
      newErrors.emergencyContact = t('tournaments.registration.errors.emergencyContactRequired');
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    setIsSubmitting(true);
    try {
      await tournamentsService.registerForTournament(formData as TournamentRegistration);
      setSubmitSuccess(true);
      setTimeout(() => {
        onSuccess?.();
        onClose();
      }, 2000);
    } catch (error: any) {
      setErrors({ submit: error.message || 'Registration failed' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={t('tournaments.registration.title')}>
      {submitSuccess && (
        <SuccessMessage>
          {t('tournaments.registration.success')}
        </SuccessMessage>
      )}

      <TournamentInfo>
        <InfoRow>
          <strong>{t('tournaments.name')}:</strong>
          <span>{tournament.name}</span>
        </InfoRow>
        <InfoRow>
          <strong>{t('tournaments.dates')}:</strong>
          <span>{formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}</span>
        </InfoRow>
        <InfoRow>
          <strong>{t('tournaments.venue')}:</strong>
          <span>{tournament.venue}</span>
        </InfoRow>
        <InfoRow>
          <strong>{t('tournaments.entryFee')}:</strong>
          <span>₹{tournament.entryFee}</span>
        </InfoRow>
        <InfoRow>
          <strong>{t('tournaments.registrationDeadline')}:</strong>
          <span>{formatDate(tournament.registrationDeadline)}</span>
        </InfoRow>
      </TournamentInfo>

      <Form onSubmit={handleSubmit}>
        <FormGroup>
          <Label htmlFor="categoryId">{t('tournaments.registration.category')} *</Label>
          <Select
            id="categoryId"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            required
          >
            <option value="">{t('tournaments.registration.selectCategory')}</option>
            {tournament.categories.map(category => (
              <option key={category.id} value={category.id}>
                {category.name} - {category.gender} ({category.ageGroup})
              </option>
            ))}
          </Select>
          {errors.categoryId && <ErrorMessage>{errors.categoryId}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="playerName">{t('tournaments.registration.playerName')} *</Label>
          <Input
            id="playerName"
            name="playerName"
            type="text"
            value={formData.playerName}
            onChange={handleChange}
            required
          />
          {errors.playerName && <ErrorMessage>{errors.playerName}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="playerEmail">{t('tournaments.registration.email')} *</Label>
          <Input
            id="playerEmail"
            name="playerEmail"
            type="email"
            value={formData.playerEmail}
            onChange={handleChange}
            required
          />
          {errors.playerEmail && <ErrorMessage>{errors.playerEmail}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="playerPhone">{t('tournaments.registration.phone')} *</Label>
          <Input
            id="playerPhone"
            name="playerPhone"
            type="tel"
            value={formData.playerPhone}
            onChange={handleChange}
            required
          />
          {errors.playerPhone && <ErrorMessage>{errors.playerPhone}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="district">{t('tournaments.registration.district')} *</Label>
          <Input
            id="district"
            name="district"
            type="text"
            value={formData.district}
            onChange={handleChange}
            required
          />
          {errors.district && <ErrorMessage>{errors.district}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="emergencyContact">{t('tournaments.registration.emergencyContact')} *</Label>
          <Input
            id="emergencyContact"
            name="emergencyContact"
            type="tel"
            value={formData.emergencyContact}
            onChange={handleChange}
            required
          />
          {errors.emergencyContact && <ErrorMessage>{errors.emergencyContact}</ErrorMessage>}
        </FormGroup>

        {errors.submit && <ErrorMessage>{errors.submit}</ErrorMessage>}

        <ButtonGroup>
          <Button type="button" variant="secondary" onClick={onClose}>
            {t('common.cancel')}
          </Button>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? t('common.submitting') : t('tournaments.registration.submit')}
          </Button>
        </ButtonGroup>
      </Form>
    </Modal>
  );
};

export default TournamentRegistrationForm;
