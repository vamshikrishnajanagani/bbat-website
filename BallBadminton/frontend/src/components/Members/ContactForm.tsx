import React, { useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { Member } from '../../store/slices/membersSlice';
import { membersService } from '../../services/membersService';
import Button from '../UI/Button';
import Modal from '../UI/Modal';

const FormContainer = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
`;

const FormGroup = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const Label = styled.label`
  display: block;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const Input = styled.input<{ hasError?: boolean }>`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ hasError, theme }) => 
    hasError ? theme.colors.error[300] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ hasError, theme }) => 
      hasError ? theme.colors.error[500] : theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ hasError, theme }) => 
      hasError ? theme.colors.error[100] : theme.colors.primary[100]};
  }
  
  &::placeholder {
    color: ${({ theme }) => theme.colors.gray[400]};
  }
`;

const TextArea = styled.textarea<{ hasError?: boolean }>`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ hasError, theme }) => 
    hasError ? theme.colors.error[300] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ hasError, theme }) => 
      hasError ? theme.colors.error[500] : theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ hasError, theme }) => 
      hasError ? theme.colors.error[100] : theme.colors.primary[100]};
  }
  
  &::placeholder {
    color: ${({ theme }) => theme.colors.gray[400]};
  }
`;

const ErrorMessage = styled.span`
  display: block;
  color: ${({ theme }) => theme.colors.error[600]};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  margin-top: ${({ theme }) => theme.spacing[1]};
`;

const MemberInfo = styled.div`
  background: ${({ theme }) => theme.colors.gray[50]};
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  
  h4 {
    margin: 0 0 ${({ theme }) => theme.spacing[2]} 0;
    color: ${({ theme }) => theme.colors.gray[900]};
    font-size: ${({ theme }) => theme.fontSizes.base};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
  }
  
  p {
    margin: 0;
    color: ${({ theme }) => theme.colors.gray[600]};
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

const SuccessMessage = styled.div`
  background: ${({ theme }) => theme.colors.success[50]};
  border: 1px solid ${({ theme }) => theme.colors.success[200]};
  color: ${({ theme }) => theme.colors.success[700]};
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  text-align: center;
  
  h4 {
    margin: 0 0 ${({ theme }) => theme.spacing[2]} 0;
    font-size: ${({ theme }) => theme.fontSizes.base};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
  }
  
  p {
    margin: 0;
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

interface ContactFormData {
  senderName: string;
  senderEmail: string;
  senderPhone: string;
  subject: string;
  message: string;
}

interface ContactFormErrors {
  senderName?: string;
  senderEmail?: string;
  subject?: string;
  message?: string;
}

interface ContactFormProps {
  member: Member;
  onSuccess?: () => void;
  onCancel?: () => void;
}

const ContactForm: React.FC<ContactFormProps> = ({
  member,
  onSuccess,
  onCancel,
}) => {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<ContactFormData>({
    senderName: '',
    senderEmail: '',
    senderPhone: '',
    subject: '',
    message: '',
  });
  const [errors, setErrors] = useState<ContactFormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const validateForm = (): boolean => {
    const newErrors: ContactFormErrors = {};

    if (!formData.senderName.trim()) {
      newErrors.senderName = t('validation.required', { field: t('members.contactForm.senderName') });
    }

    if (!formData.senderEmail.trim()) {
      newErrors.senderEmail = t('validation.required', { field: t('members.contactForm.senderEmail') });
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.senderEmail)) {
      newErrors.senderEmail = t('validation.invalidEmail');
    }

    if (!formData.subject.trim()) {
      newErrors.subject = t('validation.required', { field: t('members.contactForm.subject') });
    }

    if (!formData.message.trim()) {
      newErrors.message = t('validation.required', { field: t('members.contactForm.message') });
    } else if (formData.message.length < 10) {
      newErrors.message = t('validation.minLength', { field: t('members.contactForm.message'), length: 10 });
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field: keyof ContactFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setIsSubmitting(true);
      
      await membersService.sendContactForm({
        memberId: member.id,
        senderName: formData.senderName,
        senderEmail: formData.senderEmail,
        senderPhone: formData.senderPhone,
        subject: formData.subject,
        message: formData.message,
      });

      setIsSubmitted(true);
      
      // Auto-close after 3 seconds
      setTimeout(() => {
        onSuccess?.();
      }, 3000);
      
    } catch (err) {
      setErrors({
        message: err instanceof Error ? err.message : t('members.contactForm.submitError')
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSubmitted) {
    return (
      <FormContainer>
        <SuccessMessage>
          <h4>{t('members.contactForm.success.title')}</h4>
          <p>{t('members.contactForm.success.message', { name: member.name })}</p>
        </SuccessMessage>
        
        <Modal.Footer>
          <Button onClick={onSuccess}>
            {t('common.close')}
          </Button>
        </Modal.Footer>
      </FormContainer>
    );
  }

  return (
    <FormContainer>
      <MemberInfo>
        <h4>{t('members.contactForm.contactingMember')}</h4>
        <p>{member.name} - {member.position}</p>
      </MemberInfo>

      <form onSubmit={handleSubmit}>
        <FormGroup>
          <Label htmlFor="senderName">
            {t('members.contactForm.senderName')} *
          </Label>
          <Input
            id="senderName"
            type="text"
            value={formData.senderName}
            onChange={(e) => handleInputChange('senderName', e.target.value)}
            placeholder={t('members.contactForm.senderNamePlaceholder')}
            hasError={!!errors.senderName}
            disabled={isSubmitting}
          />
          {errors.senderName && <ErrorMessage>{errors.senderName}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="senderEmail">
            {t('members.contactForm.senderEmail')} *
          </Label>
          <Input
            id="senderEmail"
            type="email"
            value={formData.senderEmail}
            onChange={(e) => handleInputChange('senderEmail', e.target.value)}
            placeholder={t('members.contactForm.senderEmailPlaceholder')}
            hasError={!!errors.senderEmail}
            disabled={isSubmitting}
          />
          {errors.senderEmail && <ErrorMessage>{errors.senderEmail}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="senderPhone">
            {t('members.contactForm.senderPhone')}
          </Label>
          <Input
            id="senderPhone"
            type="tel"
            value={formData.senderPhone}
            onChange={(e) => handleInputChange('senderPhone', e.target.value)}
            placeholder={t('members.contactForm.senderPhonePlaceholder')}
            disabled={isSubmitting}
          />
        </FormGroup>

        <FormGroup>
          <Label htmlFor="subject">
            {t('members.contactForm.subject')} *
          </Label>
          <Input
            id="subject"
            type="text"
            value={formData.subject}
            onChange={(e) => handleInputChange('subject', e.target.value)}
            placeholder={t('members.contactForm.subjectPlaceholder')}
            hasError={!!errors.subject}
            disabled={isSubmitting}
          />
          {errors.subject && <ErrorMessage>{errors.subject}</ErrorMessage>}
        </FormGroup>

        <FormGroup>
          <Label htmlFor="message">
            {t('members.contactForm.message')} *
          </Label>
          <TextArea
            id="message"
            value={formData.message}
            onChange={(e) => handleInputChange('message', e.target.value)}
            placeholder={t('members.contactForm.messagePlaceholder')}
            hasError={!!errors.message}
            disabled={isSubmitting}
          />
          {errors.message && <ErrorMessage>{errors.message}</ErrorMessage>}
        </FormGroup>
      </form>

      <Modal.Footer>
        <Button
          variant="secondary"
          onClick={onCancel}
          disabled={isSubmitting}
        >
          {t('common.cancel')}
        </Button>
        <Button
          type="submit"
          disabled={isSubmitting}
        >
          {isSubmitting ? t('common.sending') : t('common.send')}
        </Button>
      </Modal.Footer>
    </FormContainer>
  );
};

export default ContactForm;